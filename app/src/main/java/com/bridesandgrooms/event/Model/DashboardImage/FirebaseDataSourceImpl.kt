package com.bridesandgrooms.event.Model.DashboardImage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDataSourceImpl(private val context: Context) : FirebaseDataSource {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override suspend fun fetchCategories(): List<DashboardImageData> {
        val categories = mutableListOf<DashboardImageData>()
        val snapshot =
            myRef.child("flamelink/environments/production/content/dashboardGallery/en-US").get()
                .await()
        snapshot.children.forEach { categorySnapshot ->
            val categoryName = DashboardImageData()
            categoryName.code = categorySnapshot.child("code").getValue(String::class.java)!!
            categoryName.nameEn = categorySnapshot.child("nameEn").getValue(String::class.java)!!
            categoryName.nameEs = categorySnapshot.child("nameEs").getValue(String::class.java)!!
            categoryName.descriptionEn =
                categorySnapshot.child("descriptionEn").getValue(String::class.java)!!
            categoryName.descriptionEs =
                categorySnapshot.child("descriptionEs").getValue(String::class.java)!!
            categoryName.let { categories.add(it) }
        }
        return categories
    }

    override suspend fun getAllRecentThumbnails(
        category: String,
        onlyFirst: Boolean
    ): List<Bitmap> {
        Log.d("FirebaseDataSourceImpl", "Coming into getAllRecentThumbnails")
        val localImages = getLocalImages(category)
        return if (localImages.isNotEmpty()) {
            if (onlyFirst) listOf(localImages.first()) else localImages
        } else {
            val folderPath = context.filesDir.resolve(category).absolutePath
            val fetchedImages = fetchImagesFromFirebase(category)
            Log.d(TAG, "Fetched Images: ${fetchedImages.size}")
            saveImagesLocally(category, fetchedImages)
            val updatedImages = loadImagesLocally(folderPath)
            if (onlyFirst) listOfNotNull(updatedImages.firstOrNull()) else updatedImages
        }
    }

    fun loadImagesLocally(folderPath: String): List<Bitmap> {
        val images = mutableListOf<Bitmap>()
        try {
            val folder = File(folderPath)
            if (folder.exists() && folder.isDirectory) {
                val files = folder.listFiles()
                files?.forEach { file ->
                    if (file.isFile) {
                        BitmapFactory.decodeFile(file.absolutePath)?.let { bitmap ->
                            images.add(bitmap)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions, such as IOException or OutOfMemoryError
        }
        return images
    }

    override suspend fun getPhotographerAndRegularImage(
        category: String
    ): ArrayList<DashboardImageData> {
        return fetchImagesFromFirebase(category)
    }

    fun getLocalImages(category: String): List<Bitmap> {
        Log.d("FirebaseDataSourceImpl", "Coming into getLocalImages")
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), category)
        Log.d("FirebaseDataSourceImpl", "Folder: ${folder}")
        val images = mutableListOf<Bitmap>()
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.let { files ->
                files.filter { it.isFile }.forEach { file ->
                    Log.d("FirebaseDataSourceImpl", "Path of the Bitmap file: ${file.absolutePath}")
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    images.add(bitmap)
                }
            }
        }
        Log.d("FirebaseDataSourceImpl", "This is how many images came out ${images.size}")
        return images
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun fetchImagesFromFirebase(category: String): ArrayList<DashboardImageData> {
        Log.d("FirebaseDataSourceImpl", "Entering fetchImagesFromFirebase")
        val dashboardImagesRef = myRef.child("Dashboard_Images")

        // Get the list of all child nodes (YYYYMMDD folders) under "Dashboard_Images"
        val folderNamesSnapshot = dashboardImagesRef.get().await()
        val folderNames = folderNamesSnapshot.children.mapNotNull { it.key }
        Log.d("FirebaseDataSourceImpl", "This is how many folders I got: ${folderNames.size}")
        // Sort the folder names in descending order to get the most recent one
        val latestFolderName = folderNames.sortedDescending().firstOrNull()
        Log.d("FirebaseDataSourceImpl", "This is the chosen folder: ${latestFolderName}")
        return if (latestFolderName != null) {
            val postRef = myRef.child("Dashboard_Images").child(latestFolderName).child(category)
            val dashboardImageDataList = ArrayList<DashboardImageData>()
            try {
                for (snapChild in postRef.awaitsSingle()?.children!!) {
                    val dashboardImageDataItem = DashboardImageData()
                    dashboardImageDataItem.key = snapChild.key.toString()
                    dashboardImageDataItem.photographer =
                        snapChild.child("photographer").getValue(String::class.java)!!
                    dashboardImageDataItem.regularImageUrl =
                        snapChild.child("regular").getValue(String::class.java)!!
                    dashboardImageDataItem.thumbImageUrl =
                        snapChild.child("thumb").getValue(String::class.java)!!
                    Log.d(
                        "FirebaseDataSourceImpl",
                        "First key for an image in the folder: ${dashboardImageDataItem!!.key}"
                    )
                    //dashboardImageItem!!.key = snapChild.key.toString()
                    dashboardImageDataList.add(dashboardImageDataItem)
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, e.message.toString())
            }
            dashboardImageDataList
        } else {
            ArrayList()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun fetchImageFromFirebase(category: String, key: String): DashboardImageData? {
        val dashboardImagesRef = myRef.child("Dashboard_Images")

        // Get the list of all child nodes (YYYYMMDD folders) under "Dashboard_Images"
        val folderNamesSnapshot = dashboardImagesRef.get().await()
        val folderNames = folderNamesSnapshot.children.mapNotNull { it.key }

        // Sort the folder names in descending order to get the most recent one
        val latestFolderName = folderNames.sortedDescending().firstOrNull()
        if (latestFolderName != null) {
            val postRef = myRef.child("Dashboard_Images").child(latestFolderName).child(category)
            try {
                for (snapChild in postRef.awaitsSingle()?.children!!) {
                    val dashboardImageDataItem = DashboardImageData()
                    dashboardImageDataItem.key = snapChild.key.toString()
                    dashboardImageDataItem.photographer =
                        snapChild.child("photographer").getValue(String::class.java)!!
                    dashboardImageDataItem.regularImageUrl =
                        snapChild.child("regular").getValue(String::class.java)!!
                    dashboardImageDataItem.thumbImageUrl =
                        snapChild.child("thumb").getValue(String::class.java)!!
                    if (dashboardImageDataItem.key == key) {
                        return dashboardImageDataItem
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
        return null
    }

    @ExperimentalCoroutinesApi
    suspend fun Query.awaitsSingle(): DataSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        continuation.resume(snapshot)
                    } catch (exception: java.lang.Exception) {
                        continuation.resumeWithException(exception)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = when (error.toException()) {
                        is FirebaseException -> error.toException()
                        else -> java.lang.Exception("The Firebase call for reference $this was cancelled")
                    }
                    continuation.resumeWithException(exception)
                }
            }
            continuation.invokeOnCancellation { this.removeEventListener(listener) }
            this.addListenerForSingleValueEvent(listener)
        }

    private suspend fun saveImagesLocally(category: String, images: List<DashboardImageData>) {
        withContext(Dispatchers.IO) {
            images.forEach { image ->
                saveImageLocally(category, image.thumbImageUrl)
                Log.d(
                    "FirebaseDataSourceImpl",
                    "This is where I'm supposed to get the data from: ${image.thumbImageUrl}"
                )
            }
        }
    }

    private suspend fun saveImageLocally(category: String, url: String) {
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val folder =
                        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), category)
                    folder.mkdirs()
                    val filename = "${System.currentTimeMillis()}.png"
                    val file = File(folder, filename)
                    Log.d("FirebaseDataSourceImpl", "File: ${file}")
                    val outputStream = FileOutputStream(file)
                    Log.d(
                        "FirebaseDataSourceImpl",
                        "This is where I'm supposed to get the data from: ${url}"
                    )
                    //val inputStream = context.contentResolver.openInputStream(Uri.parse(url))

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream?.close()

                    Log.d(TAG, "Image saved locally: $filename")
                } else {
                    Log.e("FirebaseDataSourceImpl", "${connection.responseCode}")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image locally: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "FirebaseDataSourceImpl"
    }
}

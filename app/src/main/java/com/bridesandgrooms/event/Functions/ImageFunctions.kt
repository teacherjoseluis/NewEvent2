package com.bridesandgrooms.event.Functions

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bridesandgrooms.event.R
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
//import kotlinx.android.synthetic.main.summary_weddinglocation.*
import java.io.*
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.isReadable


private val storage = Firebase.storage
private val storageRef = storage.reference

//Function to retrieve an image from Storage
//IN: Category of the image needed
//OUT: Image URL
fun getImgfromStorage(
    category: String,
    userid: String,
    eventid: String
): StorageReference {
    val referenceUrl = "gs://brides-n-grooms.appspot.com/images/$userid/$eventid/$category.PNG"

    try {
        return storage.getReferenceFromUrl(referenceUrl)
    } catch (e: StorageException) {
        // Log the StorageException
        Log.e("StorageException", "Error code: ${e.errorCode}, Message: ${e.message}")

        // You can return a default reference or perform any other necessary action here
        // For example, you can return null or show an error message to the user
        return storage.reference // Return a default reference or handle it as needed
    }
}

//Function to retrieve an image from Shared Preferences
//IN: Category of the image needed
//OUT: Image URL
fun getImgfromSD(category: String, context: Context): Bitmap {
    var bitmapimage: Bitmap?
    val imagepath_firstoption =
        Paths.get(
            //context.getExternalFilesDir(null)?.absolutePath, "Images", "$category.PNG"
            context.getExternalFilesDir(null)?.absolutePath, "$category.PNG"
        )
    val imagepath_secondoption =
        Paths.get(
            context.getExternalFilesDir(null)?.absolutePath, "Images", "$category.PNG"
            //context.getExternalFilesDir(null)?.absolutePath, "$category.PNG"
        )
    if (imagepath_firstoption.isReadable()) {
        bitmapimage = BitmapFactory.decodeFile(imagepath_firstoption.toString())
    } else {
        bitmapimage = BitmapFactory.decodeFile(imagepath_secondoption.toString())
    }
    if (bitmapimage == null) {
        bitmapimage = createemptyBitmap(250, 250)!!
    }
    return bitmapimage
}

/*
fun getImgfromPlaces(
    context: Context,
    placeId: String,
    ApiKey: String,
    category: String,
    placesimage: ImageView
) {
    var bitmapimage: Bitmap?
    val fields = listOf(Place.Field.PHOTO_METADATAS)

    if (!Places.isInitialized()) {
        Places.initialize(context, ApiKey, Locale.US)
    }
    val placesClient = Places.createClient(context)
    val placeRequest = FetchPlaceRequest.builder(placeId, fields).build()

    placesClient.fetchPlace(placeRequest)
        .addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place

            // Get the photo metadata.
            val metada = place.photoMetadatas
            if (metada == null || metada.isEmpty()) {
                Log.w("EventSummary.TAG", "No photo metadata.")
                return@addOnSuccessListener
            }
            val photoMetadata = metada.first()

            // Get the attribution text.
            photoMetadata?.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
                .build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener {
                    bitmapimage = it.bitmap
                    Glide.with(context)
                        .load(bitmapimage)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            @RequiresApi(Build.VERSION_CODES.R)
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                saveBitmaptoSD(context, category, bitmapimage!!)
                                return false
                            }
                        }).placeholder(R.drawable.avatar2)
                        .into(placesimage)

                    //placesimage.setImageBitmap(bitmapimage)
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e("EventSummary.TAG", "Place not found: " + exception.message)
                        bitmapimage = createemptyBitmap(250, 250)
                    }
                }
        }
}
*/

@RequiresApi(Build.VERSION_CODES.R)
suspend fun getImgfromPlaces(
    context: Context,
    placeId: String,
    apiKey: String,
    category: String
): Bitmap? {
    val fields = listOf(Place.Field.PHOTO_METADATAS)

    // Initialize Places if not already initialized
    if (!Places.isInitialized()) {
        Places.initialize(context, apiKey, Locale.US)
    }
    val placesClient = Places.createClient(context)
    val placeRequest = FetchPlaceRequest.builder(placeId, fields).build()

    return withContext(Dispatchers.IO) {
        try {
            val response = placesClient.fetchPlace(placeRequest).await()

            val place = response.place
            val metadata = place.photoMetadatas

            if (metadata == null || metadata.isEmpty()) {
                Log.w("EventSummary.TAG", "No photo metadata.")
                return@withContext null
            }
            val photoMetadata = metadata.first()
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
                .build()
            val photoResponse = placesClient.fetchPhoto(photoRequest).await()

            val bitmap = photoResponse.bitmap

            // Save bitmap to local storage
            bitmap?.let {
                saveBitmaptoSD(context, category, bitmap)
            }

            return@withContext bitmap
        } catch (e: Exception) {
            if (e is ApiException) {
                Log.e("EventSummary.TAG", "Place not found: ${e.message}")
            }
            return@withContext null
        }
    }
}


//Function to store an image to Storage
//IN: Category, userId, EventId
fun saveImgtoStorage(category: String, userid: String, eventid: String, uri: Uri) {
    val imageRef = storageRef.child("images/$userid/$eventid/$category.PNG")
    val uploadTask = imageRef.putFile(uri)

    uploadTask.addOnFailureListener { exception ->
        // Handle the exception and log the error
        Log.e("StorageException", "Error uploading $category image: ${exception.message}", exception)
    }.addOnSuccessListener { taskSnapshot ->
        // Handle the success case if needed
        // You can access the download URL of the uploaded file using taskSnapshot.metadata?.reference?.downloadUrl
    }
}

fun saveURLImgtoSD(category: String, uri: Uri?, context: Context) {
    val path = "Images"
    val downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    try {
        val request: DownloadManager.Request = DownloadManager.Request(uri)
            .setTitle(category)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(context, path, "$category.PNG")
        downloadManager.enqueue(request)
        Log.d("ImageFunctions", "Download $category image from Firebase to Local Storage")
    } catch (e: Exception) {
        // Handle the exception and log the error
        Log.e("ImageFunctions", "Error downloading $category image: ${e.message}", e)
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun saveBitmaptoSD(context: Context, category: String, bitmap: Bitmap) {
    val imagepath = Paths.get(
        context.getExternalFilesDir(null)?.absolutePath
    ).toString()
    val imagefile = File(imagepath, "$category.PNG")
    try {
        //Request permissions
        //permission already granted
        val out = FileOutputStream(imagefile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the event ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun saveImagetoSD(context: Context, category: String, imageUri: Uri) {
    val imagepath = Paths.get(context.getExternalFilesDir(null)?.absolutePath).toString()
    val imagefile = File(imagepath, "$category.PNG")
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val outputStream = FileOutputStream(imagefile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error saving the image: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun replaceImage(context: Context, category: String, userid: String, eventid: String, uri: Uri) {
    // Replace Image consists of:
    // 1. Delete Image from SD
    delImgfromSD(category, context)
    // 2. Rewrite Image in Storage
    saveImgtoStorage(category, userid, eventid, uri)
    saveImagetoSD(context, category, uri)
}

//Function to delete/replace and image from Shared Preferences
//IN: Category, EventId
@RequiresApi(Build.VERSION_CODES.O)
fun delImgfromSD(category: String, context: Context) {
    val imagepath =
        Paths.get(
            context.getExternalFilesDir(null)?.absolutePath
        ).toString()
    val imagefile = File(imagepath, "$category.PNG")
    imagefile.delete()
}

fun createemptyBitmap(w: Int, h: Int): Bitmap? {
    val conf = Bitmap.Config.ARGB_8888
    return Bitmap.createBitmap(w, h, conf)
}

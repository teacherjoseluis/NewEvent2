package com.example.newevent2.Functions

import Application.Cache
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths


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
    return storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/$userid/$eventid/$category.PNG")
}

//Function to retrieve an image from Shared Preferences
//IN: Category of the image needed
//OUT: Image URL
@RequiresApi(Build.VERSION_CODES.O)
fun getImgfromSD(category: String, context: Context): Bitmap {
    val imagepath =
    Paths.get(
        context.getExternalFilesDir(null)?.absolutePath, "Images",  "$category.PNG").toString()
    var bitmapimage = BitmapFactory.decodeFile(imagepath)
    if (bitmapimage == null) {
        bitmapimage = createemptyBitmap(250, 250)
    }
    return bitmapimage
}

//Function to store an image to Storage
//IN: Category, userId, EventId
fun saveImgtoStorage(category: String, userid: String, eventid: String, uri: Uri) {
    val imageRef = storageRef.child("images/$userid/$eventid/$category.PNG")
    val uploadTask = imageRef.putFile(uri)

    uploadTask.addOnFailureListener {
        return@addOnFailureListener
    }.addOnSuccessListener {
        return@addOnSuccessListener
    }
}

fun saveURLImgtoSD(category: String, uri: Uri?, context: Context) {
    val path = "Images"
    val downloadmanager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request: DownloadManager.Request = DownloadManager.Request(uri)
        .setTitle(category)
        .setDescription("Downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationInExternalFilesDir(context, path, "$category.PNG")
    val downloadId = downloadmanager.enqueue(request)
    Log.d("ImageFunctions", "Download $category image from Firebase to Local Storage")
}


@RequiresApi(Build.VERSION_CODES.O)
fun replaceImage(context: Context, category: String, userid: String, eventid: String, uri: Uri) {
    // Replace Image consists of:
    // 1. Delete Image from SD
    delImgfromSD(category, context)
    // 2. Rewrite Image in Storage
    saveImgtoStorage(category, userid, eventid, uri)
}

//Function to delete/replace and image from Shared Preferences
//IN: Category, EventId
@RequiresApi(Build.VERSION_CODES.O)
fun delImgfromSD(category: String, context: Context) {
    val imagepath =
        Paths.get(
            context.getExternalFilesDir(null)?.absolutePath, "Images").toString()
    val imagefile = File(imagepath, "$category.PNG")
    imagefile.delete()
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    var bitmap: Bitmap? = null
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }
    bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun getDefaultImage(context: Context): Bitmap? {
    val defaultimage = Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.resources.getResourcePackageName(R.drawable.avatar2)
                + '/' + context.resources.getResourceTypeName(R.drawable.avatar2) + '/' + context.resources.getResourceEntryName(
            R.drawable.avatar2
        )
    )
    return MediaStore.Images.Media.getBitmap(context.contentResolver, defaultimage)
}

fun uritoBitmap(context: Context, uri: Uri): Bitmap? {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
}

fun createemptyBitmap(w: Int, h: Int): Bitmap? {
    val conf = Bitmap.Config.ARGB_8888
    return Bitmap.createBitmap(w, h, conf)
}


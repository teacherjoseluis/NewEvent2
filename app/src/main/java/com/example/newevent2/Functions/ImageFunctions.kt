package com.example.newevent2.Functions

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.newevent2.R
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.summary_weddinglocation.*
import java.io.*
import java.nio.file.Paths
import java.util.*


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
            context.getExternalFilesDir(null)?.absolutePath, "Images", "$category.PNG"
        ).toString()
    var bitmapimage = BitmapFactory.decodeFile(imagepath)
    if (bitmapimage == null) {
        bitmapimage = createemptyBitmap(250, 250)
    }
    return bitmapimage
}

fun getImgfromPlaces(
    context: Context,
    placeId: String,
    ApiKey: String,
    category: String,
    placesimage : ImageView
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
    downloadmanager.enqueue(request)
    Log.d("ImageFunctions", "Download $category image from Firebase to Local Storage")
}

fun saveBitmaptoSD(context: Context, category: String, bitmap: Bitmap) {
    val imagepath = Paths.get(
        context.getExternalFilesDir(null)?.absolutePath, "Images"
    ).toString()
    val imagefile = File(imagepath, "$category.PNG")
    val out = FileOutputStream(imagefile)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    out.flush()
    out.close()
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
            context.getExternalFilesDir(null)?.absolutePath, "Images"
        ).toString()
    val imagefile = File(imagepath, "$category.PNG")
    imagefile.delete()
}

fun createemptyBitmap(w: Int, h: Int): Bitmap? {
    val conf = Bitmap.Config.ARGB_8888
    return Bitmap.createBitmap(w, h, conf)
}


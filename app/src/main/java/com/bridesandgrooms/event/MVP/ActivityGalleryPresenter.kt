package com.bridesandgrooms.event.MVP

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.bridesandgrooms.event.UI.Fragments.FragmentGallery
import com.bridesandgrooms.event.Model.DashboardImage.DashboardImageResult
import com.bridesandgrooms.event.Model.DashboardImage.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ActivityGalleryPresenter(val activity: FragmentGallery)  {

    private lateinit var repository: DashboardRepository

    fun setRepository(repository: DashboardRepository) {
        this.repository = repository
    }


//    fun getEventchildrenflag(): Boolean {
//        //This function needs to return a boolean
//        val eventdbhelper = EventDBHelper(context)
//        val event = eventdbhelper.getEvent()
//        return presenterevent.getEventChildrenflag(event.key)
//    }

//    fun fetchDashboardImages() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val categories = repository.fetchCategories()
//            val dashboardImages = mutableListOf<List<Bitmap>>()
//            categories.forEach {
//                val image = getAllRecentThumbnails(it)
//                dashboardImages.add(image!!)
//            }
//            fragment.onDashboardImages(dashboardImages)
//        }
//    }

    fun fetchActivityGallery(category: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val deferredImages =  async {
                    getAllCategoryImages(category)
                }
            val imageList = deferredImages.await()
            activity.onActiveGalleryImages(imageList!!)
        }
    }

    private suspend fun getAllCategoryImages(category: String): List<Triple<Bitmap, String, String>>? {
        return withContext(Dispatchers.Main) {
            when (val result = repository.getPhotographerAndRegularImage(category)) {
                is DashboardImageResult.SuccessURL -> {
                    val imageList = mutableListOf<Triple<Bitmap, String, String>>()
                    for (image in result.images!!) {
                        val bitmap = downloadBitmap(image.regularImageUrl)
                        if (bitmap != null) {
                            imageList.add(Triple(bitmap, image.photographer, image.regularImageUrl))
                        }
                    }
                    imageList
                }
                else -> {
                    Log.e("DashboardEventPresenter", "Error: $result")
                    null
                }
            }
        }
    }


    private suspend fun downloadBitmap(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10000 // 10 seconds timeout
                connection.readTimeout = 15000 // 15 seconds read timeout

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    bitmap
                } else {
                    Log.e("DashboardEventPresenter", "Server returned HTTP error code: ${connection.responseCode}")
                    null
                }
            } catch (e: IOException) {
                Log.e("DashboardEventPresenter", "Error downloading image: ${e.message}")
                null
            }
        }
    }


    interface ActiveGalleryImages {
        fun onActiveGalleryImages(
            images: List<Triple<Bitmap, String, String>>
        )

        fun onActiveGalleryImagesError(errcode: String)
    }

}


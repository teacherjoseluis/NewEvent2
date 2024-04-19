package com.bridesandgrooms.event.Model.DashboardImage

import android.graphics.Bitmap
import android.util.Log

class DashboardRepository(private val firebaseDataSource: FirebaseDataSource) {
//    suspend fun getFirstRecentThumbnail(category: String): DashboardImageResult {
//        return try {
//            val images = firebaseDataSource.getFirstRecentThumbnails(category)
//            DashboardImageResult.Success(images)
//        } catch (e: Exception) {
//            DashboardImageResult.Error(e.message ?: "An error occurred")
//        }
//    }

    suspend fun fetchCategories(): List<DashboardImageData> {
        return firebaseDataSource.fetchCategories()
    }

    suspend fun getAllRecentThumbnails(category: String, onlyFirst: Boolean): DashboardImageResult {
        return try {
            Log.d("DashboardRepository", "Coming into getAllRecentThumbnails")
            val images = firebaseDataSource.getAllRecentThumbnails(category, onlyFirst)
            DashboardImageResult.Success(images, category)
        } catch (e: Exception) {
            DashboardImageResult.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getPhotographerAndRegularImage(category: String): DashboardImageResult {
        return try {
            val images = firebaseDataSource.getPhotographerAndRegularImage(category)
            DashboardImageResult.SuccessURL(images)
        } catch (e: Exception) {
            DashboardImageResult.Error(e.message ?: "An error occurred")
        }
    }
}

interface FirebaseDataSource {
    //suspend fun getFirstRecentThumbnails(category: String): List<DashboardImage>
    suspend fun fetchCategories(): List<DashboardImageData>
    suspend fun getAllRecentThumbnails(category: String, onlyFirst: Boolean): List<Bitmap>
    suspend fun getPhotographerAndRegularImage(category: String): ArrayList<DashboardImageData>
}
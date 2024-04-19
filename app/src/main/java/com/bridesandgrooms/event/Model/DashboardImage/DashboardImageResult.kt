package com.bridesandgrooms.event.Model.DashboardImage

import android.graphics.Bitmap

sealed class DashboardImageResult {
    data class Success(val images: List<Bitmap>, val category: String) : DashboardImageResult()
    data class SuccessURL(val images: List<DashboardImageData>?) : DashboardImageResult()
    data class Error(val message: String) : DashboardImageResult()
}

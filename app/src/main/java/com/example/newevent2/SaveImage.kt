package com.example.newevent2

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.checkSelfPermission
import java.io.File
import java.io.FileOutputStream

internal fun saveImage(context: Context, imageuri: Uri) {

    val cw = ContextWrapper(context)

    var file = cw.getDir("images", Context.MODE_PRIVATE)
    file = File(file, imageuri.lastPathSegment.toString())


    try {
        val parcelFileDescriptor = cw.contentResolver.openFileDescriptor(imageuri, "r");
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

        val fos = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        Log.i("File Saved",file.toString())
        fos.flush()
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
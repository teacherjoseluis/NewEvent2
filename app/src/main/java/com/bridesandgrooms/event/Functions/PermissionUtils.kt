package com.bridesandgrooms.event.Functions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings.Global.getString
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TaskCreateEdit

// PermissionUtils.kt

object PermissionUtils {
    const val PERMISSION_CODE = 100 // Define your desired permission request code here

    fun checkPermissions(context: Context): Boolean {
        return ((checkSelfPermission(context, Manifest.permission.READ_CALENDAR) ==
                PackageManager.PERMISSION_GRANTED
                ) && (checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) ==
                PackageManager.PERMISSION_GRANTED
                ) && (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                ) && (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                ))
    }

    fun requestPermissions(activity: Activity) {
        val permissions =
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        //show popup to request runtime permission
        ActivityCompat.requestPermissions(activity, permissions, TaskCreateEdit.PERMISSION_CODE)
    }

    fun alertBox(context: Context) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.lackpermissions_message))
        builder.setMessage(context.getString(R.string.lackpermissions_message))

        builder.setPositiveButton(
            context.getString(R.string.accept)
        ) { _, _ ->
            requestPermissions(context as Activity)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ -> p0!!.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }
}

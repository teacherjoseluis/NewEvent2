package com.bridesandgrooms.event.Functions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Fragments.TaskCreateEdit

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
                ) && (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                ) && (checkSelfPermission(context, Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED
                ))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions(context: Context, permissionType: String): Boolean {
        return when (permissionType) {
            "calendar" -> {
                checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
            }
            "storage" -> {
                checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
            "contact" -> {
                checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
            }
            "location" -> {
                checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
            }
            "notification" -> {
                checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // Handle other permission types here if needed
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions(activity: Activity) {
        val permissions =
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.POST_NOTIFICATIONS
            )
        //show popup to request runtime permission
        ActivityCompat.requestPermissions(activity, permissions, TaskCreateEdit.PERMISSION_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissionsList(permissionType: String) : Array<String>  {
        val permissions = when (permissionType) {
            "calendar" -> arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            )
            "storage" -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            "contact" -> arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
            "location" -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            )
            "notification" -> arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
            else -> arrayOf() // Return an empty array if an unsupported permission type is provided
        }
        return permissions
    }

    fun alertBox(context: Context) : Boolean {
        val builder = android.app.AlertDialog.Builder(context)
        var response = false
        builder.setTitle(context.getString(R.string.lackpermissions_message))
        builder.setMessage(context.getString(R.string.lackpermissions_message))

        builder.setPositiveButton(
            context.getString(R.string.accept)
        ) { _, _ ->
            ///requestPermissions(context as Activity, permissionType)
            response = true
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ -> p0!!.dismiss() }

        val dialog = builder.create()
        dialog.show()
        return response
    }
}

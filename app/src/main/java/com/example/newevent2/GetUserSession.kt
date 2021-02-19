package com.example.newevent2

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

internal fun getUserSession(context: Context): List<String> {

    var sharedPreference: SharedPreferences? = null
    sharedPreference = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

    val UserId = sharedPreference!!.getString("UID", "")!!
    val Email = sharedPreference!!.getString("Email", "")!!
    val Authtype = sharedPreference!!.getString("Autentication", "")!!
    val EventId = sharedPreference!!.getString("Eventid", "")!!
    val ShortName = sharedPreference!!.getString("Shortname", "")!!
    val Role = sharedPreference!!.getString("Role", "")!!
    val Imageurl = sharedPreference!!.getString("Imageurl", "")!!
    return listOf(UserId, Email, Authtype, EventId, ShortName, Role, Imageurl)
}
package com.example.newevent2.Functions

import android.content.Context
import android.content.SharedPreferences

internal fun getUserSession(context: Context): String {

    var sharedPreference: SharedPreferences? = null
    sharedPreference = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    return sharedPreference!!.getString("UID", "")!!
}
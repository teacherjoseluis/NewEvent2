package com.example.newevent2.Functions

import android.content.Context
import android.content.SharedPreferences
import com.example.newevent2.Model.User

//deberia estar regresando un objeto del tipo user
internal fun getUserSession(context: Context): User {

    var sharedPreference: SharedPreferences? = null
    val user = User()
    sharedPreference = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    user.key = sharedPreference!!.getString("key", "")!!
    user.eventid = sharedPreference!!.getString("eventid", "")!!
    user.shortname = sharedPreference!!.getString("shortname", "")!!
    user.email = sharedPreference!!.getString("email", "")!!
    user.country = sharedPreference!!.getString("country", "")!!
    user.language = sharedPreference!!.getString("language", "")!!
    user.createdatetime = sharedPreference!!.getString("createdatetime", "")!!
    user.authtype = sharedPreference!!.getString("authtype", "")!!
    user.status = sharedPreference!!.getString("status", "")!!
    user.imageurl = sharedPreference!!.getString("imageurl", "")!!
    user.role = sharedPreference!!.getString("role", "")!!
    user.hasevent = sharedPreference!!.getString("hasevent", "")!!
    user.hastask = sharedPreference!!.getString("hastask", "")!!
    user.haspayment = sharedPreference!!.getString("haspayment", "")!!
    user.hasguest = sharedPreference!!.getString("hasguest", "")!!
    user.hasvendor = sharedPreference!!.getString("hasvendor", "")!!
    user.tasksactive = sharedPreference!!.getInt("tasksactive", 0)!!
    user.taskscompleted = sharedPreference!!.getInt("taskscompleted", 0)!!
    user.payments = sharedPreference!!.getInt("payments", 0)!!
    return user
}
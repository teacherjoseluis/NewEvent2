package com.bridesandgrooms.event.Functions

import android.content.Context
import android.content.SharedPreferences

object UserSessionHelper {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences =
            context.applicationContext.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    }

    fun getUserSession(category: String): Any {
        return when (category) {
            "email" -> sharedPreferences.getString("email", "")!!
            "user_id" -> sharedPreferences.getString("user_id", "")!!
            "event_id" -> sharedPreferences.getString("event_id", "")!!
            "session_id" -> sharedPreferences.getString("session_id", "")!!
            "last_signed_in_at" -> sharedPreferences.getLong("last_signed_in_at", 0L)
            else -> ""
        }
    }

    fun getUserSessionInfo(category: String): Any {
        return when (category) {
            "email" -> sharedPreferences.getString("email", "")!!
            "user_id" -> sharedPreferences.getString("user_id", "")!!
            "event_id" -> sharedPreferences.getString("event_id", "")!!
            "session_id" -> sharedPreferences.getString("session_id", "")!!
            "last_signed_in_at" -> sharedPreferences.getLong("last_signed_in_at", 0L)
            else -> ""
        }
    }

    fun saveUserSession(
        value: String?,
        valueLong: Long?,
        category: String
    ) {
        val sessionEditor = sharedPreferences.edit()
        when (category) {
            "email" -> sessionEditor.putString("email", value)
            "user_id" -> sessionEditor.putString("user_id", value)
            "event_id" -> sessionEditor.putString("event_id", value)
            "session_id" -> sessionEditor.putString("session_id", value)
            "last_signed_in_at" -> sessionEditor.putLong("last_signed_in_at", valueLong!!)
        }
        sessionEditor.apply()
    }

    fun deleteUserSession() {
        val sessionEditor = sharedPreferences.edit()
        sessionEditor.putString("email", "")
        sessionEditor.putString("user_id", "")
        sessionEditor.putString("event_id", "")
        sessionEditor.putString("session_id", "")
        sessionEditor.putLong("last_signed_in_at", 0L)
        sessionEditor.apply()
    }
}
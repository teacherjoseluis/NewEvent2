package com.example.newevent2.Functions

import android.annotation.SuppressLint
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
internal fun getCurrentDateTime(): SimpleDateFormat {
    val timestamp = Time(System.currentTimeMillis())
    val taskdatetime = Date(timestamp.time)
    return SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
}
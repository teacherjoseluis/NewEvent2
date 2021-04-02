package com.example.newevent2.Functions

import android.annotation.SuppressLint
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
internal fun getCurrentDateTime(): String {
    val timestamp = Time(System.currentTimeMillis())
    val taskdatetime = Date(timestamp.time)
    val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
    return sdf.format(taskdatetime)
}
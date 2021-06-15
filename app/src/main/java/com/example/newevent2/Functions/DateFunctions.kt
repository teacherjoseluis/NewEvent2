package com.example.newevent2.Functions

import android.os.Build
import androidx.annotation.RequiresApi
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

internal fun converttoDate(datestring: String): Date {
    val pattern = "dd/MM/yyyy" //Pattern in which dates are saved in Firebase
    val locale = Locale.getDefault()
    val simpleDateFormat = SimpleDateFormat(pattern, locale)
    //val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, locale) returns a String
    return simpleDateFormat.parse(datestring)
}

internal fun converttoString(date: Date, style: Int): String {
    val locale = Locale.getDefault()
    val dateFormatter = DateFormat.getDateInstance(style, locale)
    return dateFormatter.format(date)
}

internal fun daystoDate(date: Date): Int {
    val toDate = Date()
    val diff = date.time - toDate.time
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun validateOldDate(year: Int, month: Int, day: Int): Boolean {
    val localDate = LocalDate.of(year, month, day)
    val defaultZoneId = ZoneId.systemDefault()
    val datetovalidate = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
    return datetovalidate.after(currentDateTime)
}

internal val currentDateTime: Date
    get() {
        val timestamp = Time(System.currentTimeMillis())
        return Date(timestamp.time)
    }
package com.bridesandgrooms.event.Functions

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Model.EventDBHelper
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
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

internal fun converttoCalendar(date: Date): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

internal fun converttoString(date: Date, style: Int): String {
    val locale = Locale.getDefault()
    val dateFormatter = DateFormat.getDateInstance(style, locale)
    return dateFormatter.format(date)
}

internal fun getlocale() : String {
    return Locale.getDefault().toString()
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
    //val datetovalidate = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
    val datetovalidate = Date.from(localDate.atTime(LocalTime.MAX).atZone(defaultZoneId).toInstant())
    return datetovalidate.after(currentDateTime)
}

internal val currentDateTime: Date
    get() {
        val timestamp = Time(System.currentTimeMillis())
        return Date(timestamp.time)
    }

internal fun isEventDate(mContext: Context): Int {
//    var isEventDay = 1
//    val user = com.example.newevent2.Functions.getUserSession(mContext!!)
//    val eventmodel = EventModel()
//    eventmodel.getEventdetail(user.key, user.eventid,
//        object : EventModel.FirebaseSuccessListenerEventDetail {
//            override fun onEvent(event: Event) {
//                val converteddate = converttoDate(event.date)
//                isEventDay = daystoDate(converteddate)
//            }
//        })
    val eventdb = EventDBHelper(mContext)
    val event = eventdb.getEvent()

    //0 if it's the day of the event
    //> 1, days have passed since the event
    //< 1, days yet to pass since the event
    return if (event.date == "") {
        1
    } else {
        daystoDate(converttoDate(event.date))
    }
}

internal fun getMockUserSetTime(): Pair<Int, Int> {
    val calendar = Calendar.getInstance().apply {
//            // add just 1 min from current time
        add(Calendar.MINUTE, 1)
    }
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}

// Calculate time difference relative to current time in ms
internal fun calculateTimeDifferenceMs(hourOfDay: Int, minute: Int): Long {
    val now = Calendar.getInstance()
    val then = (now.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
    }
    return then.timeInMillis - now.timeInMillis
}
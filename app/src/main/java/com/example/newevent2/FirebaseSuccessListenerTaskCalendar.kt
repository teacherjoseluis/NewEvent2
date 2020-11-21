package com.example.newevent2
import java.util.*
import kotlin.collections.ArrayList

interface FirebaseSuccessListenerTaskCalendar {
        fun onTasksDatesEvent(list: ArrayList<Date>)
        fun onTasksperDay(list: ArrayList<Task>)
}
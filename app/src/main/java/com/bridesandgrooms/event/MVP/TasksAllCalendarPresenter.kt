package com.bridesandgrooms.event.MVP

import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.UI.Fragments.TasksAllCalendar
import java.util.Date

class TasksAllCalendarPresenter(
    val context: Context,
    val fragment: TasksAllCalendar
) {

    fun getDateTaskArray(date: Date) {
        val taskDBHelper = TaskDBHelper(context)
        try {
            val taskArray = taskDBHelper.getDateTaskArray(date)
            fragment.onTaskArray(taskArray)
        } catch (e: Exception) {
            Log.e("TasksAllCalendarPresenter", e.message.toString())
            fragment.onTaskArrayError(ERRCODETASKS)
        }
    }

    interface TaskArrayInterface {

        fun onTaskArray(
            list: ArrayList<Task>?
        )

        fun onTaskArrayError(errcode: String)
    }

}


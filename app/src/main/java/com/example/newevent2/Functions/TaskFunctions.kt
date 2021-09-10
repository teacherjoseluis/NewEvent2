package com.example.newevent2.Functions

import Application.CalendarEvent
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeleteTask
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.Model.UserModel
import com.example.newevent2.NotificationID
import com.example.newevent2.NotificationJobService
import com.google.gson.Gson
import java.util.*

private lateinit var calendarevent : CalendarEvent
var taskmodel = TaskModel()
lateinit var taskdbhelper: TaskDBHelper
private lateinit var usermodel: UserModel

internal fun addTask(context: Context, taskitem: Task) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = com.example.newevent2.Functions.getUserSession(context!!)
        taskmodel.userid = user.key
        taskmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        taskdbhelper = TaskDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        val chainofcommand = orderChainAdd(calendarevent, taskmodel, taskdbhelper, usermodel)
        chainofcommand.onAddEditTask(taskitem)
        //------------------------------------------------
        // Updating User information in Session
        user.tasksactive = user.tasksactive + 1
        user.hastask = TaskModel.ACTIVEFLAG
        user.saveUserSession(context)
        //------------------------------------------------
        Toast.makeText(context, "Task was created successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the task ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deleteTask(context: Context, taskitem: Task) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        val user = getUserSession(context!!)
        taskmodel.userid = user.key
        taskmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        taskdbhelper = TaskDBHelper(context)
        taskdbhelper.task = taskitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        // Updating User information in Session
        user.tasksactive = user.tasksactive - 1
        if (user.tasksactive == 0) user.hastask = TaskModel.INACTIVEFLAG
        user.saveUserSession(context)

        val chainofcommand =
            orderChainDel(calendarevent, usermodel, taskdbhelper, taskmodel)
        chainofcommand.onDeleteTask(taskitem)
        //------------------------------------------------
        Toast.makeText(context, "Task was deleted successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying delete the task ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editTask(context: Context, taskitem: Task) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //---------------------------------------------------
        val user = getUserSession(context!!)
        taskmodel.userid = user.key
        taskmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        taskdbhelper = TaskDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        val chainofcommand = orderChainEdit(calendarevent, taskmodel, taskdbhelper)
        chainofcommand.onAddEditTask(taskitem)
        //------------------------------------------------
        Toast.makeText(context, "Task was edited successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying edit the task ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    taskModel: TaskModel,
    taskDBHelper: TaskDBHelper,
    userModel: UserModel
): CoRAddEditTask {
    calendarEvent.nexthandlert = taskModel
    taskModel.nexthandler = taskDBHelper
    taskDBHelper.nexthandler = userModel
    return calendarEvent
}

private fun orderChainDel(
    calendarEvent: CalendarEvent,
    userModel: UserModel,
    taskDBHelper: TaskDBHelper,
    taskModel: TaskModel
): CoRDeleteTask {
    calendarEvent.nexthandlertdel = userModel
    userModel.nexthandlerdelt = taskDBHelper
    taskDBHelper.nexthandlerdel = taskModel
    return calendarEvent
}

private fun orderChainEdit(
    calendarEvent: CalendarEvent,
    taskModel: TaskModel,
    taskDBHelper: TaskDBHelper
): CoRAddEditTask {
    calendarEvent.nexthandlert = taskModel
    taskModel.nexthandler = taskDBHelper
    return calendarEvent
}

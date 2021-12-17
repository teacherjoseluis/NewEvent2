package com.example.newevent2.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeleteTask
import com.example.newevent2.Model.*
import com.example.newevent2.R

@SuppressLint("StaticFieldLeak")
private lateinit var calendarevent : CalendarEvent
var taskmodel = TaskModel()
@SuppressLint("StaticFieldLeak")
private lateinit var taskdbhelper: TaskDBHelper
private lateinit var usermodel: UserModel

internal fun addTask(context: Context, taskitem: Task) {
    try {
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = getUserSession(context)
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
        //--------------------------------------------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", taskitem.category)
        bundle.putString("BUDGET", taskitem.budget)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDTASK", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successaddtask), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroraddtask)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
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
        val user = getUserSession(context)
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
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", taskitem.category)
        bundle.putString("BUDGET", taskitem.budget)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("DELETETASK", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successdeletetask), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.errordeletetask)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
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
        val user = getUserSession(context)
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
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", taskitem.category)
        bundle.putString("BUDGET", taskitem.budget)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITTASK", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successedittask), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroredittask)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
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

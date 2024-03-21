package com.bridesandgrooms.event.Functions

import Application.CalendarEvent
import Application.TaskCreationException
import Application.TaskDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addTask(context: Context, userItem: User, taskItem: Task) {
    try {
        val calendarevent = CalendarEvent(context)
        val userdbhelper = UserDBHelper(context)
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand =
            orderChainAdd(calendarevent, taskmodel, taskdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditTask(context, userItem, taskItem)
    } catch (e: Exception) {
        Log.e("TaskFunctions.kt", e.message.toString())
        throw TaskCreationException("Error during task Creation: $e")
    }
}

internal fun deleteTask(context: Context, userItem: User, taskItem: Task) {
    try {
        val calendarevent = CalendarEvent(context)
        val userdbhelper = UserDBHelper(context)
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand =
            orderChainDel(calendarevent, usermodel, userdbhelper, taskdbhelper, taskmodel)
        chainofcommand.onDeleteTask(context, userItem, taskItem)
    } catch (e: Exception) {
        Log.e("TaskFunctions.kt", e.message.toString())
        throw TaskDeletionException("Error during task Deletion: $e")
    }
}

internal fun editTask(context: Context, userItem: User, taskItem: Task) {
    try {
        val calendarevent = CalendarEvent(context)
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper(context)

        val chainofcommand = orderChainEdit(calendarevent, taskmodel, taskdbhelper)
        chainofcommand.onAddEditTask(context, userItem, taskItem)
    } catch (e: Exception) {
        Log.e("TaskFunctions.kt", e.message.toString())
        throw TaskCreationException("Error during task Edition: $e")
    }
}

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    taskModel: TaskModel,
    taskDBHelper: TaskDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel
): CoRAddEditTask {
    calendarEvent.nexthandlert = taskModel
    taskModel.nexthandler = taskDBHelper
    taskDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandlert = userModel
    return calendarEvent
}

private fun orderChainDel(
    calendarEvent: CalendarEvent,
    userModel: UserModel,
    userdbhelper: UserDBHelper,
    taskDBHelper: TaskDBHelper,
    taskModel: TaskModel
): CoRDeleteTask {
    calendarEvent.nexthandlertdel = userModel
    userModel.nexthandlerdelt = userdbhelper
    userdbhelper.nexthandlerdelt = taskDBHelper
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

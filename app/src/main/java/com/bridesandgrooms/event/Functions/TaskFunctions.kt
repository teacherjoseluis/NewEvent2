package com.bridesandgrooms.event.Functions

import Application.CalendarCreationException
import Application.CalendarEditionException
import Application.CalendarEvent
import Application.EventCreationException
import Application.TaskCreationException
import Application.TaskDeletionException
import Application.UserCreationException
import Application.UserEditionException
import Application.UserOnboardingException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addTask(taskItem: Task) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val userdbhelper = UserDBHelper()
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper()
        val usermodel = UserModel()

        val chainofcommand =
            orderChainAdd(calendarevent, taskmodel, taskdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditTask(taskItem)
        //-------------------------------------------------------
    } catch (e: UserEditionException) {
        throw TaskCreationException("Error while trying to edit the User: $e")
    } catch (e: CalendarEditionException) {
        throw TaskCreationException("Error while trying to add the Task to the local Calendar: $e")
        //-------------------------------------------------------
    } catch (e: Exception) {
        Log.e("TaskFunctions.kt", e.message.toString())
        throw TaskCreationException("Error during task Creation: $e")
    }
}

internal fun deleteTask(taskId: String) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val userdbhelper = UserDBHelper()
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper()
        val usermodel = UserModel()

        val chainofcommand =
            orderChainDel(calendarevent, usermodel, userdbhelper, taskdbhelper, taskmodel)
        chainofcommand.onDeleteTask(taskId)
    } catch (e: Exception) {
        Log.e("TaskFunctions.kt", e.message.toString())
        throw TaskDeletionException("Error during task Deletion: $e")
    }
}

internal fun editTask(taskItem: Task) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val taskmodel = TaskModel()
        val taskdbhelper = TaskDBHelper()

        val chainofcommand = orderChainEdit(calendarevent, taskmodel, taskdbhelper)
        chainofcommand.onAddEditTask(taskItem)
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

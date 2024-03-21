package com.bridesandgrooms.event.Functions

import Application.CalendarEvent
import Application.UserEditionException
import Application.UserOnboardingException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal suspend fun onBoarding(context: Context, userItem: User, eventitem: Event) {
    try {
        val calendarevent = CalendarEvent(context)
        val userdbhelper = UserDBHelper(context)
        val eventmodel = EventModel()
        val eventdbhelper = EventDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand =
            orderChainOnboard(usermodel, userdbhelper, calendarevent, eventmodel, eventdbhelper)
        chainofcommand.onOnboardUser(userItem, eventitem)
    } catch (e: Exception) {
        Log.e("UserFunctions.kt", e.message.toString())
        throw UserOnboardingException("Error during user onboarding: $e")
    }
}

internal suspend fun editUser(context: Context, useritem: User) {
    try {
        val userdbhelper = UserDBHelper(context)
        val usermodel = UserModel(useritem)

        val chainofcommand = orderChainEdit(usermodel, userdbhelper)
        chainofcommand.onAddEditUser(useritem)
    } catch (e: Exception) {
        Log.e("UserFunctions.kt", e.message.toString())
        throw UserEditionException("Error during user edition: $e")
    }
}

private fun orderChainEdit(
    userModel: UserModel,
    userDBHelper: UserDBHelper
): CoRAddEditUser {
    userModel.nexthandleru = userDBHelper
    return userModel
}

private fun orderChainOnboard(
    userModel: UserModel,
    userDBHelper: UserDBHelper,
    calendarEvent: CalendarEvent,
    eventModel: EventModel,
    eventDBHelper: EventDBHelper
): CoROnboardUser {
    userModel.nexthandleron = userDBHelper
    userDBHelper.nexthandleron = calendarEvent
    calendarEvent.nexthandleron = eventModel
    eventModel.nexthandleron = eventDBHelper
    return userModel
}

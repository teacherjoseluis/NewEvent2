package com.bridesandgrooms.event.Functions

import Application.CalendarCreationException
import Application.CalendarEvent
import Application.EventCreationException
import Application.UserAuthenticationException
import Application.UserCreationException
import Application.UserEditionException
import Application.UserOnboardingException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal suspend fun onBoarding(userItem: User, eventitem: Event) {
    try {
        val calendarevent = CalendarEvent.getInstance()
        val userdbhelper = UserDBHelper()
        val eventmodel = EventModel()
        val eventdbhelper = EventDBHelper()
        val usermodel = UserModel()

        val chainofcommand =
            orderChainOnboard(usermodel, userdbhelper, calendarevent, eventmodel, eventdbhelper)
        chainofcommand.onOnboardUser(userItem, eventitem)
    } catch (e: UserCreationException) {
        throw UserOnboardingException("Error while trying to create the User: $e")
    } catch (e: EventCreationException) {
        throw UserOnboardingException("Error while trying to create the Event: $e")
    } catch (e: CalendarCreationException) {
        throw UserOnboardingException("Error while trying to add the Event to the local Calendar: $e")
    } catch (e: Exception) {
        Log.e("UserFunctions.kt", e.message.toString())
        throw UserOnboardingException("Error during user onboarding: $e")
    }
}

internal suspend fun editUser(useritem: User) {
    try {
        val userdbhelper = UserDBHelper()
        val usermodel = UserModel()

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

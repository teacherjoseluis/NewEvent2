package com.bridesandgrooms.event.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.bridesandgrooms.event.CoRAddEditUser
import com.bridesandgrooms.event.CoROnboardUser
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.R

private lateinit var usermodel: UserModel
@SuppressLint("StaticFieldLeak")
private lateinit var calendarevent : CalendarEvent
@SuppressLint("StaticFieldLeak")
lateinit var userdbhelper: UserDBHelper

internal suspend fun onBoarding(context: Context, useritem: User, eventitem: Event){
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Adding a new record in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        eventmodel.userid = user.userid!!
        //------------------------------------------------
        // Adding a new record in Local DB
        eventdbhelper = EventDBHelper(context)
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(useritem.userid)
        //------------------------------------------------
        val chainofcommand = orderChainOnboard(usermodel,userdbhelper,calendarevent, eventmodel, eventdbhelper)
        chainofcommand.onOnboardUser(useritem, eventitem)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successadduser), Toast.LENGTH_LONG).show()
        Toast.makeText(context, context.getString(R.string.eventcreated), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroradduser)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal suspend fun editUser(context: Context, useritem: User) {
    try {
        // Adding a new record in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(useritem.userid)
        //------------------------------------------------
        val chainofcommand = orderChainEdit(usermodel,userdbhelper)
        chainofcommand.onAddEditUser(useritem)
        //------------------------------------------------

        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("AUTHTYPE", useritem.authtype)
        bundle.putString("COUNTRY", useritem.country)
        bundle.putString("LANGUAGE", useritem.language)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITUSER", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successedituser), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroredituser)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun orderChainAdd(
    userModel: UserModel,
    userDBHelper: UserDBHelper
): CoRAddEditUser {
    userModel.nexthandleru = userDBHelper
    return userModel
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
package com.example.newevent2.Functions

import Application.CalendarEvent
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.*
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Event
import com.google.firebase.analytics.FirebaseAnalytics

private lateinit var calendarevent : CalendarEvent
var eventmodel = EventModel()
lateinit var eventdbhelper: EventDBHelper
private lateinit var usermodel: UserModel

internal fun addEvent(context: Context, eventitem: Event) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = com.example.newevent2.Functions.getUserSession(context!!)
        eventmodel.userid = user.key
        //------------------------------------------------
        // Adding a new record in Local DB
        eventdbhelper = EventDBHelper(context)
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        val chainofcommand = orderChainAdd(calendarevent, eventmodel, eventdbhelper, usermodel)
        chainofcommand.onAddEditEvent(eventitem)
        //------------------------------------------------
        // Updating User information in Session
        //user.saveUserSession(context)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("LOCATION", eventitem.location)
        bundle.putString("DATE", eventitem.date)
        bundle.putString("TIME", eventitem.date)
        bundle.putDouble("LATITUDE", eventitem.latitude)
        bundle.putDouble("LONGITUDE", eventitem.longitude)
        MyFirebaseApp.mFirebaseAnalytics!!.logEvent("ADDEVENT", bundle)
        //------------------------------------------------
        Toast.makeText(context, "Event was created successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the event ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editEvent(context: Context, eventitem: Event) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //---------------------------------------------------
        val user = com.example.newevent2.Functions.getUserSession(context!!)
        user.eventid = eventitem.key
        user.hasevent = "Y"
        //------------------------------------------------
        // Adding a new record in Local DB
        eventdbhelper = EventDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------

        val chainofcommand = orderChainEdit(calendarevent, eventmodel, eventdbhelper, usermodel)
        chainofcommand.onAddEditEvent(eventitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("LOCATION", eventitem.location)
        bundle.putString("DATE", eventitem.date)
        bundle.putString("TIME", eventitem.date)
        bundle.putDouble("LATITUDE", eventitem.latitude)
        bundle.putDouble("LONGITUDE", eventitem.longitude)
        MyFirebaseApp.mFirebaseAnalytics!!.logEvent("EDITEVENT", bundle)
        //------------------------------------------------
        Toast.makeText(context, "Event was edited successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying edit the event ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    eventModel: EventModel,
    eventDBHelper: EventDBHelper,
    userModel: UserModel
): CoRAddEditEvent {
    calendarEvent.nexthandlere = eventModel
    eventModel.nexthandlere = eventDBHelper
    eventDBHelper.nexthandlere = userModel
    return calendarEvent
}

private fun orderChainEdit(
    calendarEvent: CalendarEvent,
    eventModel: EventModel,
    eventDBHelper: EventDBHelper,
    userModel: UserModel
): CoRAddEditEvent {
    calendarEvent.nexthandlere = eventModel
    eventModel.nexthandlere = eventDBHelper
    eventDBHelper.nexthandlere = userModel
    return calendarEvent
}

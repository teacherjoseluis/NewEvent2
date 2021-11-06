package com.example.newevent2.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.*
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Event

@SuppressLint("StaticFieldLeak")
private lateinit var calendarevent : CalendarEvent
@SuppressLint("StaticFieldLeak")
var eventmodel = EventModel()
@SuppressLint("StaticFieldLeak")
lateinit var eventdbhelper: EventDBHelper
private lateinit var usermodel: UserModel

internal fun addEvent(context: Context, eventitem: Event) {
    try {
        //------------------------------------------------
        // Adding Calendar Event
        calendarevent = CalendarEvent(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = getUserSession(context)
        eventmodel.userid = user.key
        //------------------------------------------------
        // Adding a new record in Local DB
        eventdbhelper = EventDBHelper(context)
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //------------------------------------------------
        val chainofcommand = orderChainAdd(calendarevent, eventmodel, eventdbhelper)
        chainofcommand.onAddEditEvent(eventitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("LOCATION", eventitem.location)
        bundle.putString("DATE", eventitem.date)
        bundle.putString("TIME", eventitem.date)
        bundle.putDouble("LATITUDE", eventitem.latitude)
        bundle.putDouble("LONGITUDE", eventitem.longitude)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDEVENT", bundle)
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

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    eventModel: EventModel,
    eventDBHelper: EventDBHelper
): CoRAddEditEvent {
    calendarEvent.nexthandlere = eventModel
    eventModel.nexthandlere = eventDBHelper
    return calendarEvent
}


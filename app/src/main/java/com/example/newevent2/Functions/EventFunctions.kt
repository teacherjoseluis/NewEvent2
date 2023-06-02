package com.example.newevent2.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import com.example.newevent2.CoRAddEditEvent
import com.example.newevent2.Model.EventDBHelper
import com.example.newevent2.Model.EventModel
import com.example.newevent2.Model.UserModel

@SuppressLint("StaticFieldLeak")
private lateinit var calendarevent : CalendarEvent
@SuppressLint("StaticFieldLeak")
var eventmodel = EventModel()
@SuppressLint("StaticFieldLeak")
lateinit var eventdbhelper: EventDBHelper
private lateinit var usermodel: UserModel

private fun orderChainAdd(
    calendarEvent: CalendarEvent,
    eventModel: EventModel,
    eventDBHelper: EventDBHelper
): CoRAddEditEvent {
    calendarEvent.nexthandlere = eventModel
    eventModel.nexthandlere = eventDBHelper
    return calendarEvent
}


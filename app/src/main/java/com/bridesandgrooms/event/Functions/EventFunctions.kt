package com.bridesandgrooms.event.Functions

import Application.CalendarEvent
import android.annotation.SuppressLint
import com.bridesandgrooms.event.Model.EventDBHelper
import com.bridesandgrooms.event.Model.EventModel
import com.bridesandgrooms.event.Model.UserModel

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


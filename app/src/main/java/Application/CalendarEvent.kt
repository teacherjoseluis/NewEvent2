package Application

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import com.bridesandgrooms.event.*
import com.bridesandgrooms.event.Functions.CoRAddEditEvent
import com.bridesandgrooms.event.Functions.CoRAddEditPayment
import com.bridesandgrooms.event.Functions.CoRAddEditTask
import com.bridesandgrooms.event.Functions.CoRDeletePayment
import com.bridesandgrooms.event.Functions.CoRDeleteTask
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.converttoCalendar
import com.bridesandgrooms.event.Functions.converttoDate
import com.bridesandgrooms.event.Model.DatabaseHelper
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.GuestDBHelper
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.User
import java.util.*


class CalendarEvent private constructor(context: Context) : CoRAddEditTask, CoRDeleteTask, CoRAddEditPayment,
    CoRDeletePayment, CoRAddEditEvent, CoROnboardUser {

    private lateinit var calendaruri: String
    private lateinit var begindate: Calendar
    private lateinit var eventUri: Uri

    private var appContext = context.applicationContext
    var nexthandlere: CoRAddEditEvent? = null
    var nexthandlert: CoRAddEditTask? = null
    var nexthandlertdel: CoRDeleteTask? = null
    var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandleron: CoROnboardUser? = null


    private fun addEvent(item: Any) {
        var event = ContentValues()
        when (item) {
            is Event -> {
                val eventdate = converttoDate(item.date)
                begindate = converttoCalendar(eventdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, item.address)
            }

            is Task -> {
                val taskdate = converttoDate(item.date)
                begindate = converttoCalendar(taskdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, "Task for the ${item.category} category")
            }

            is Payment -> {
                val paymentdate = converttoDate(item.date)
                begindate = converttoCalendar(paymentdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, "Payment for the ${item.category} category")
            }
        }
        val calendarId = getCalendarId()
        if (calendarId != null) {
            event.put(Events.CALENDAR_ID, calendarId)
            event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
            event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
            event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
            event.put(Events.ORGANIZER, "B&G")
            event.put(Events.ALL_DAY, 1)
            event.put(Events.STATUS, 1)
            event.put(Events.HAS_ALARM, 1)
            try {
                appContext.contentResolver.insert(Events.CONTENT_URI, event)
            } catch (e: Exception) {
                throw CalendarCreationException(e.toString())
            }
        }
    }

    private fun editEvent(item: Any) {
        var event: ContentValues = ContentValues()
        when (item) {
            is Task -> {
                if (item.eventid != "") {
                    val taskdate = converttoDate(item.date)
                    begindate = converttoCalendar(taskdate)
                    eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())

                    event.put(Events.TITLE, item.name)
                    event.put(Events.DESCRIPTION, "Task for the ${item.category} category")
                    event.put(Events.CALENDAR_ID, getCalendarId())
                    event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                    event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.ORGANIZER, "B&G")
                    event.put(Events.ALL_DAY, 1)
                    event.put(Events.STATUS, 1)
                    event.put(Events.HAS_ALARM, 1)

                    try {
                        appContext.contentResolver.update(eventUri, event, null, null)
                    } catch (e: Exception) {
                        throw CalendarEditionException(e.toString())
                    }
                }
            }

            is Payment -> {
                if (item.eventid != "") {
                    val paymentdate = converttoDate(item.date)
                    begindate = converttoCalendar(paymentdate)
                    eventUri =
                        ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())

                    event.put(Events.TITLE, item.name)
                    event.put(Events.DESCRIPTION, "Payment for the ${item.category} category")
                    event.put(Events.CALENDAR_ID, getCalendarId())
                    event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                    event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.ORGANIZER, "B&G")
                    event.put(Events.ALL_DAY, 1)
                    event.put(Events.STATUS, 1)
                    event.put(Events.HAS_ALARM, 1)

                    try {
                        appContext.contentResolver.update(eventUri, event, null, null)
                    } catch (e: Exception) {
                        throw CalendarEditionException(e.toString())
                    }
                }
            }
        }
    }

    private fun deleteEvent(item: Any) {
        when (item) {
            is Task -> {
                if (item.eventid != "") {
                    eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())
                    appContext.contentResolver.delete(eventUri, null, null)
                }
            }

            is Payment -> {
                if (item.eventid != "") {
                    eventUri =
                        ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())
                    appContext.contentResolver.delete(eventUri, null, null)
                }
            }
        }
    }

    private fun getCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        var calCursor = appContext.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
            null,
            CalendarContract.Calendars._ID + " ASC"
        )

        if (calCursor != null && calCursor.count <= 0) {
            calCursor = appContext.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )
        }

        if (calCursor != null) {
            if (calCursor.moveToFirst()) {
                val calID: String
                val idCol = calCursor.getColumnIndex(projection[0])
                calID = calCursor.getString(idCol)

                calCursor.close()
                return calID.toLong()
            }
        }
        return null
    }

    @SuppressLint("Range")
    private fun getNewEventId(): Long {
        val localuri = Uri.parse(getCalendarUriBase() + "events")
        val cursor: Cursor? = appContext.contentResolver.query(
            localuri!!,
            arrayOf("MAX(_id) as max_id"),
            null,
            null,
            "_id"
        )
        cursor!!.moveToFirst()
        val eventidlong = cursor.getLong(cursor.getColumnIndex("max_id"))
        cursor.close()
        return eventidlong
    }

    private fun getCalendarUriBase(): String {
        var calendarUriBase: String? = null
        var calendars = Uri.parse("content://calendar/calendars")
        var managedCursor: Cursor? = null
        try {
            managedCursor = appContext.contentResolver.query(
                calendars!!,
                null, null, null, null
            )
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/"
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars")
            try {
                managedCursor = appContext.contentResolver.query(
                    calendars,
                    null, null, null, null
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/"
            }
        }
        calendaruri = calendarUriBase!!
        managedCursor?.close()
        return calendarUriBase
    }

    companion object {
        const val TAG = "CalendarEvent"

        private lateinit var instance: CalendarEvent

        fun initialize(context: Context) {
            instance = CalendarEvent(context)
        }

        fun getInstance(): CalendarEvent {
            if (!::instance.isInitialized) {
                throw IllegalStateException("CalendarEvent is not initialized. Call initialize() first.")
            }
            return instance
        }
    }

    override fun onAddEditTask(task: Task) {
        if (task.key.isEmpty()) {
            addEvent(task)
            task.eventid = getNewEventId().toString()
            nexthandlert?.onAddEditTask(task)
        } else {
            editEvent(task)
            nexthandlert?.onAddEditTask(task)
        }
    }

    override fun onDeleteTask(taskId: String) {
        deleteEvent(taskId)
        nexthandlertdel?.onDeleteTask(taskId)
    }

    override fun onAddEditPayment(payment: Payment) {
        if (payment.key.isEmpty()) {
            addEvent(payment)
            payment.eventid = getNewEventId().toString()
            nexthandlerp?.onAddEditPayment(payment)
        } else {
            editEvent(payment)
            nexthandlerp?.onAddEditPayment(payment)
        }
    }

    override fun onDeletePayment(paymentId: String) {
        deleteEvent(paymentId)
        nexthandlerpdel?.onDeletePayment(paymentId)
    }

    override suspend fun onAddEditEvent(event: Event) {
        addEvent(event)
        event.eventid = getNewEventId().toString()
        nexthandlere?.onAddEditEvent(event)
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        addEvent(event)
        event.eventid = getNewEventId().toString()
        nexthandleron?.onOnboardUser(user, event)
    }
}




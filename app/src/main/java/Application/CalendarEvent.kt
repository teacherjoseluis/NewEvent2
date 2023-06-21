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
import com.example.newevent2.*
import com.example.newevent2.Functions.converttoCalendar
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.User
import java.util.*


class CalendarEvent(val context: Context) : CoRAddEditTask, CoRDeleteTask, CoRAddEditPayment,
    CoRDeletePayment, CoRAddEditEvent, CoROnboardUser {

    private lateinit var calendaruri: String
    private lateinit var begindate: Calendar
    private lateinit var eventUri: Uri

    var event: ContentValues = ContentValues()

    var nexthandlere: CoRAddEditEvent? = null
    var nexthandlert: CoRAddEditTask? = null
    var nexthandlertdel: CoRDeleteTask? = null
    var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null
    var nexthandleron: CoROnboardUser? = null

    val cr: ContentResolver = context.contentResolver

    private fun addEvent(item: Any) {
        when (item) {
            is Event -> {
                val eventdate = converttoDate(item.date)
                begindate = converttoCalendar(eventdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, item.address)
                //event.put(Events.UID_2445, item.key)
            }
            is Task -> {
                val taskdate = converttoDate(item.date)
                begindate = converttoCalendar(taskdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, "Task for the ${item.category} category")
                //event.put(Events.UID_2445, item.key)
            }
            is Payment -> {
                val paymentdate = converttoDate(item.date)
                begindate = converttoCalendar(paymentdate)

                event.put(Events.TITLE, item.name)
                event.put(Events.DESCRIPTION, "Payment for the ${item.category} category")
                //event.put(Events.UID_2445, item.key)
            }
        }
        val calendarId = getCalendarId(context)
        if (calendarId != null) {
            event.put(Events.CALENDAR_ID, calendarId)
            event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
            event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
            event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
            event.put(Events.ORGANIZER, "B&G")
            event.put(Events.ALL_DAY, 1)
            event.put(Events.STATUS, 1)
            event.put(Events.HAS_ALARM, 1)

            cr.insert(Events.CONTENT_URI, event)
        }
    }

    private fun editEvent(item: Any) {
        when (item) {
            is Task -> {
                if (item.eventid != "") {
                    val taskdate = converttoDate(item.date)
                    begindate = converttoCalendar(taskdate)
                    eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())

                    event.put(Events.TITLE, item.name)
                    event.put(Events.DESCRIPTION, "Task for the ${item.category} category")
                    event.put(Events.CALENDAR_ID, getCalendarId(context))
                    event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                    event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.ORGANIZER, "B&G")
                    event.put(Events.ALL_DAY, 1)
                    event.put(Events.STATUS, 1)
                    event.put(Events.HAS_ALARM, 1)

                    cr.update(eventUri, event, null, null)
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
                    event.put(Events.CALENDAR_ID, getCalendarId(context))
                    event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                    event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
                    event.put(Events.ORGANIZER, "B&G")
                    event.put(Events.ALL_DAY, 1)
                    event.put(Events.STATUS, 1)
                    event.put(Events.HAS_ALARM, 1)

                    cr.update(eventUri, event, null, null)
                }
            }
        }
    }

    private fun deleteEvent(item: Any) {
        when (item) {
            is Task -> {
                if (item.eventid != "") {
                    eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())
                    cr.delete(eventUri, null, null)
                }
            }

            is Payment -> {
                if (item.eventid != "") {
                    eventUri =
                        ContentUris.withAppendedId(Events.CONTENT_URI, item.eventid.toLong())
                    cr.delete(eventUri, null, null)
                }
            }
        }
    }

    private fun getCalendarId(context: Context): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        var calCursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
            null,
            CalendarContract.Calendars._ID + " ASC"
        )

        if (calCursor != null && calCursor.count <= 0) {
            calCursor = context.contentResolver.query(
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
        val cursor: Cursor? = context.contentResolver.query(
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
            managedCursor = context.contentResolver.query(
                calendars!!,
                null, null, null, null
            )
        } catch (e: Exception) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/"
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars")
            try {
                managedCursor = context.contentResolver.query(
                    calendars,
                    null, null, null, null
                )
            } catch (e: Exception) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/"
            }
        }
        calendaruri = calendarUriBase!!
        managedCursor?.close()
        return calendarUriBase
    }

    override fun onAddEditTask(task: Task) {
        if (task.key == "") {
            addEvent(task)
            task.eventid = getNewEventId().toString()
            nexthandlert?.onAddEditTask(task)
        } else if (task.key != "") {
            editEvent(task)
            nexthandlert?.onAddEditTask(task)
        }
    }

    override fun onDeleteTask(task: Task) {
        deleteEvent(task)
        nexthandlertdel?.onDeleteTask(task)
    }

    override fun onAddEditPayment(payment: Payment) {
        if (payment.key == "") {
            addEvent(payment)
            payment.eventid = getNewEventId().toString()
            nexthandlerp?.onAddEditPayment(payment)
        } else if (payment.key != "") {
            editEvent(payment)
            nexthandlerp?.onAddEditPayment(payment)
        }
    }

    override fun onDeletePayment(payment: Payment) {
        deleteEvent(payment)
        nexthandlerpdel?.onDeletePayment(payment)
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




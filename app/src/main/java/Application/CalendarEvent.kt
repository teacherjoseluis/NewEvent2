package Application

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import com.example.newevent2.CoRAddEditPayment
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeletePayment
import com.example.newevent2.CoRDeleteTask
import com.example.newevent2.Functions.converttoCalendar
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.Model.Payment
import com.example.newevent2.Model.Task
import java.util.*


class CalendarEvent(val context: Context) : CoRAddEditTask, CoRDeleteTask, CoRAddEditPayment,
    CoRDeletePayment {

    lateinit var calendar_uri: String
    lateinit var begindate: Calendar
    lateinit var eventUri: Uri

    var event: ContentValues = ContentValues()

    var nexthandlert: CoRAddEditTask? = null
    var nexthandlertdel: CoRDeleteTask? = null
    var nexthandlerp: CoRAddEditPayment? = null
    var nexthandlerpdel: CoRDeletePayment? = null

    val cr: ContentResolver = context.contentResolver

    fun addEvent(item: Any) {
        when (item) {
            is Task -> {
                var task = item
                val taskdate = converttoDate(task.date)
                begindate = converttoCalendar(taskdate)

                event.put(Events.TITLE, task.name)
                event.put(Events.DESCRIPTION, "Task for the ${task.category} category")
            }
            is Payment -> {
                var payment = item
                val paymentdate = converttoDate(payment.date)
                begindate = converttoCalendar(paymentdate)

                event.put(Events.TITLE, payment.name)
                event.put(Events.DESCRIPTION, "Payment for the ${payment.category} category")
            }
        }
        event.put(Events.CALENDAR_ID, getCalendarId(context))
        event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        event.put(Events.DTSTART, begindate.timeInMillis + 60 * 60 * 1000)
        event.put(Events.DTEND, begindate.timeInMillis + 60 * 60 * 1000)
        event.put(Events.ORGANIZER, "B&G")
        event.put(Events.ALL_DAY, 1)
        event.put(Events.STATUS, 1)
        event.put(Events.HAS_ALARM, 1)

        cr.insert(Events.CONTENT_URI, event)
    }

    fun editEvent(item: Any) {
        when (item) {
            is Task -> {
                var task = item
                if (task.eventid != "") {
                    val taskdate = converttoDate(task.date)
                    begindate = converttoCalendar(taskdate)
                    eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, task.eventid.toLong())

                    event.put(Events.TITLE, task.name)
                    event.put(Events.DESCRIPTION, "Task for the ${task.category} category")
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
                var payment = item
                if (payment.eventid != "") {
                    val paymentdate = converttoDate(payment.date)
                    begindate = converttoCalendar(paymentdate)
                    eventUri =
                        ContentUris.withAppendedId(Events.CONTENT_URI, payment.eventid.toLong())

                    event.put(Events.TITLE, payment.name)
                    event.put(Events.DESCRIPTION, "Payment for the ${payment.category} category")
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

    fun deleteEvent(item: Any) {
        when (item) {
            is Task -> {
                var task = item
                eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, task.eventid.toLong())
            }

            is Payment -> {
                var payment = item
                eventUri =
                    ContentUris.withAppendedId(Events.CONTENT_URI, payment.eventid.toLong())
            }
        }
        cr.delete(eventUri, null, null)
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
                val calName: String
                val calID: String
                val nameCol = calCursor.getColumnIndex(projection[1])
                val idCol = calCursor.getColumnIndex(projection[0])

                calName = calCursor.getString(nameCol)
                calID = calCursor.getString(idCol)

                //Log.d("Calendar name = $calName Calendar ID = $calID")

                calCursor.close()
                return calID.toLong()
            }
        }
        return null
    }

    private fun getNewEventId(): Long {
        var local_uri = Uri.parse(getCalendarUriBase() + "events")
        val cursor: Cursor? = context.contentResolver.query(
            local_uri!!,
            arrayOf("MAX(_id) as max_id"),
            null,
            null,
            "_id"
        )
        cursor!!.moveToFirst()
        val max_val: Long = cursor.getLong(cursor.getColumnIndex("max_id"))
        return max_val
    }

    fun getCalendarUriBase(): String? {
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
        calendar_uri = calendarUriBase!!
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
}




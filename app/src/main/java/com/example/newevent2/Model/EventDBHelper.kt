package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.newevent2.CoRAddEditEvent
import com.example.newevent2.CoROnboardUser
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Functions.userdbhelper

class EventDBHelper(val context: Context) : CoRAddEditEvent, CoROnboardUser {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    var nexthandlere: CoRAddEditEvent? = null
    var nexthandleron: CoROnboardUser? = null

    fun insert(event: Event) {
        val values = ContentValues()
        values.put("eventid", event.key)
        values.put("imageurl", event.imageurl)
        values.put("placeid", event.placeid)
        values.put("latitude", event.latitude)
        values.put("longitude", event.longitude)
        values.put("address", event.address)
        values.put("name", event.name)
        values.put("date", event.date)
        values.put("time", event.time)
        values.put("about", event.eventid)
        values.put("location", event.location)
        db.insert("EVENT", null, values)
        Log.d(TAG, "Event record inserted")

        //updating eventid in user
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        user.eventid = event.key
        userdbhelper = UserDBHelper(context)
        userdbhelper.update(user)
//        user.saveUserSession(context)
    }

    private fun getEventexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM EVENT WHERE eventid = '$key'", null)
        if (cursor.count > 0) {
            existsflag = true
        }
        cursor.close()
        return existsflag
    }

    fun getEvent(): Event {
//        val list = ArrayList<Event>()
        var event = Event()
        val cursor: Cursor = db.rawQuery("SELECT * FROM EVENT", null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                event.key = cursor.getString(cursor.getColumnIndex("eventid"))
                event.imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))
                event.placeid = cursor.getString(cursor.getColumnIndex("placeid"))
                event.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                event.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                event.address = cursor.getString(cursor.getColumnIndex("address"))
                event.name = cursor.getString(cursor.getColumnIndex("name"))
                event.date = cursor.getString(cursor.getColumnIndex("date"))
                event.time = cursor.getString(cursor.getColumnIndex("time"))
                event.eventid = cursor.getString(cursor.getColumnIndex("about"))
                event.location = cursor.getString(cursor.getColumnIndex("location"))
//                event =
//                    Event(eventid, imageurl, placeid, latitude, longitude, address, name, date, time, about,location)
//                    list.add(event)
                Log.d(TAG, "Event ${event.key} record obtained from local DB")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return event
    }

    fun getEventChildrenflag(key: String): Boolean {
        var existsflag = false
        var cursor: Cursor = db.rawQuery("SELECT * FROM TASK WHERE eventid = '$key'", null)
        if (cursor.count > 0) {
            existsflag = true
        }
        cursor.close()
        if (!existsflag) {
            cursor = db.rawQuery("SELECT * FROM PAYMENT WHERE eventid = '$key'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
        }
        return existsflag
    }

    fun update(event: Event) {
        val values = ContentValues()
        values.put("eventid", event.key)
        values.put("imageurl", event.imageurl)
        values.put("placeid", event.placeid)
        values.put("latitude", event.latitude)
        values.put("longitude", event.longitude)
        values.put("address", event.address)
        values.put("name", event.name)
        values.put("date", event.date)
        values.put("time", event.time)
        values.put("about", event.eventid)
        values.put("location", event.location)

        val retVal = db.update("EVENT", values, "eventid = '${event.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Event ${event.key} updated")
        } else {
            Log.d(TAG, "Event ${event.key} not updated")
        }
        //db.close()
    }

    fun delete(event: Event) {
        val retVal = db.delete("EVENT", "eventid = '${event.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Event ${event.key} deleted")
        } else {
            Log.d(TAG, "Event ${event.key} not deleted")
        }
        //db.close()
    }

    companion object {
        const val TAG = "EventDBHelper"
    }

    override suspend fun onAddEditEvent(event: Event) {
        if (!getEventexists(event.key)) {
            insert(event)
        } else {
            update(event)
        }
        nexthandlere?.onAddEditEvent(event)
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        if (!getEventexists(event.key)) {
            insert(event)
        } else {
            update(event)
        }
        nexthandleron?.onOnboardUser(user, event)
    }
}

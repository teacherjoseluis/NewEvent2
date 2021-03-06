package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class EventDBHelper(val context: Context) {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    fun insert(event: Event) {
        var values = ContentValues()
        values.put("eventid", event.key)
        values.put("imageurl", event.imageurl)
        values.put("placeid", event.placeid)
        values.put("latitude", event.latitude)
        values.put("longitude", event.longitude)
        values.put("address", event.address)
        values.put("name", event.name)
        values.put("date", event.date)
        values.put("time", event.time)
        values.put("about", event.about)
        values.put("location", event.location)
        db.insert("EVENT", null, values)
        Log.d(TAG, "Event record inserted")
    }

    fun getEvent(): Event {
//        val list = ArrayList<Event>()
        var event = Event()
        val cursor: Cursor = db.rawQuery("SELECT * FROM EVENT", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))
                    val placeid = cursor.getString(cursor.getColumnIndex("placeid"))
                    val latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                    val longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                    val address = cursor.getString(cursor.getColumnIndex("address"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val time = cursor.getString(cursor.getColumnIndex("time"))
                    val about = cursor.getString(cursor.getColumnIndex("about"))
                    val location = cursor.getString(cursor.getColumnIndex("location"))
                    event =
                        Event(eventid, imageurl, placeid, latitude, longitude, address, name, date, time, about,location)
//                    list.add(event)
                    Log.d(TAG, "Event $eventid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return event
    }

    fun update(event: Event) {
        var values = ContentValues()
        values.put("eventid", event.key)
        values.put("imageurl", event.imageurl)
        values.put("placeid", event.placeid)
        values.put("latitude", event.latitude)
        values.put("longitude", event.longitude)
        values.put("address", event.address)
        values.put("name", event.name)
        values.put("date", event.date)
        values.put("time", event.time)
        values.put("about", event.about)
        values.put("location", event.location)

        val retVal = db.update("EVENT", values, "eventid = '${event.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Event ${event.key} updated")
        } else {
            Log.d(TAG, "Event ${event.key} not updated")
        }
        db.close()
    }

    fun delete(event: Event) {
        val retVal = db.delete("EVENT", "eventid = '${event.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Event ${event.key} deleted")
        } else {
            Log.d(TAG, "Event ${event.key} not deleted")
        }
        db.close()
    }

    companion object {
        const val TAG = "EventDBHelper"
    }
}

package com.bridesandgrooms.event.Model

import Application.EventCreationException
import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.Functions.CoRAddEditEvent
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.UserSessionHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

class EventDBHelper: CoRAddEditEvent, CoROnboardUser{

    private var nexthandlere: CoRAddEditEvent? = null
    private var nexthandleron: CoROnboardUser? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport() : Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val event: Event
        try {
            val eventModel = EventModel()
            event = eventModel.getEvent()
            //update(event)
            Log.d(TAG, "Event record deleted ${event.key}")
            db.execSQL("DELETE FROM EVENT")
            insert(event)
        } catch (e: Exception){
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Event data: $e")
//        } finally {
//            db.close()
        }
        return true
    }

    fun insert(event: Event) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
        try {
            db.insert("EVENT", null, values)
            Log.d(TAG, "Event record inserted ${event.key}")
        } catch (e: Exception) {
            throw EventCreationException(e.toString())
//        } finally {
//            db.close()
        }

        //updating eventid in user
        val userDBHelper = UserDBHelper()
        userDBHelper.editUserEvent(event.key)
        UserSessionHelper.saveUserSession(event.key, null, "event_id")
    }

    private fun getEventexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM EVENT WHERE eventid = '$key'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
            return existsflag
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return false
//        } finally {
//            db.close()
        }
    }

    @SuppressLint("Range")
    fun getEvent(): Event? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
//        val list = ArrayList<Event>()
        var event = Event()
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun update(event: Event) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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

        try {
            val retVal = db.update("EVENT", values, "eventid = '${event.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Event ${event.key} updated")
            } else {
                Log.d(TAG, "Event ${event.key} not updated")
            }
            //db.close()
        } catch (e: Exception) {
            throw EventCreationException(e.toString())
//        } finally {
//            db.close()
        }
    }

    fun delete(event: Event) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        try {
            val retVal = db.delete("EVENT", "eventid = '${event.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Event ${event.key} deleted")
            } else {
                Log.d(TAG, "Event ${event.key} not deleted")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        }
//        finally {
//            db.close()
        }
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

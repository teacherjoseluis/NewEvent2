package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.Functions.CoRAddEditEvent
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.saveUserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi

class EventDBHelper(val context: Context) : CoRAddEditEvent, CoROnboardUser{

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    private var nexthandlere: CoRAddEditEvent? = null
    private var nexthandleron: CoROnboardUser? = null


    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(user: User) : Boolean {
        val event: Event
        try {
            val eventModel = EventModel()
            event = eventModel.getEvent(user.userid!!)
            //update(event)
            Log.d(TAG, "Event record deleted ${event.key}")
            db.execSQL("DELETE FROM EVENT")
            insert(event)
        } catch (e: Exception){
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Event data: $e")
        } finally {
            db.close()
        }
        return true
    }

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
        try {
            db.insert("EVENT", null, values)
            Log.d(TAG, "Event record inserted ${event.key}")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }

        //updating eventid in user
        val userdbhelper = UserDBHelper(context)
        val user = User().getUser(context)
        user.eventid = event.key
        userdbhelper.update(user)
//        user.saveUserSession(context)
        saveUserSession(context, event.key, null, "event_id")
    }

    private fun getEventexists(key: String): Boolean {
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
        } finally {
            db.close()
        }
    }

    @SuppressLint("Range")
    fun getEvent(): Event? {
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
        } finally {
            db.close()
        }
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

        try {
            val retVal = db.update("EVENT", values, "eventid = '${event.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Event ${event.key} updated")
            } else {
                Log.d(TAG, "Event ${event.key} not updated")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }

    fun delete(event: Event) {
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
        }
        finally {
            db.close()
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

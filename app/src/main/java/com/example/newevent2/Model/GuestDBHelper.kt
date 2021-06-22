package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class GuestDBHelper(val context: Context) {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    fun insert(guest: Guest) {
        var values = ContentValues()
        values.put("guestid", guest.key)
        values.put("eventid", guest.eventid)
        values.put("contactid", guest.contactid)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)
        db.insert("GUEST", null, values)
        Log.d(TAG, "Guest record inserted")
    }

    fun getGuest(): ArrayList<Guest> {
        val list = ArrayList<Guest>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val guestid = cursor.getString(cursor.getColumnIndex("guestid"))
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val contactid = cursor.getString(cursor.getColumnIndex("contactid"))
                    val rsvp = cursor.getString(cursor.getColumnIndex("rsvp"))
                    val companion = cursor.getString(cursor.getColumnIndex("companion"))
                    val tableguest = cursor.getString(cursor.getColumnIndex("tableguest"))
                    val guest =
                        Guest(eventid, contactid, rsvp, companion, tableguest)
                    list.add(guest)
                    Log.d(TAG, "Guest $guestid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun update(guest: Guest) {
        var values = ContentValues()
        values.put("guestid", guest.key)
        values.put("eventid", guest.eventid)
        values.put("contactid", guest.contactid)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)

        val retVal = db.update("GUEST", values, "guestid = " + guest.key, null)
        if (retVal >= 1) {
            Log.d(TAG, "Guest ${guest.key} updated")
        } else {
            Log.d(TAG, "Guest ${guest.key} not updated")
        }
        db.close()
    }

    fun delete(guest: Guest) {
        val retVal = db.delete("GUEST", "guestid = " + guest.key, null)
        if (retVal >= 1) {
            Log.d(TAG, "Guest ${guest.key} deleted")
        } else {
            Log.d(TAG, "Guest ${guest.key} not deleted")
        }
        db.close()
    }

    companion object {
        const val TAG = "GuestDBHelper"
    }
}

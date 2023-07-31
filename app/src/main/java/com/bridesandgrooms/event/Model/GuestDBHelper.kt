package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.CoRAddEditGuest
import com.bridesandgrooms.event.CoRDeleteGuest
import kotlinx.coroutines.ExperimentalCoroutinesApi

class GuestDBHelper(context: Context) : CoRAddEditGuest, CoRDeleteGuest {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var guest: Guest
    var key = ""
    var nexthandler: CoRAddEditGuest? = null
    var nexthandlerdel: CoRDeleteGuest? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String) : Boolean {
        val guestList: ArrayList<Guest>
        val eventModel = EventModel()
        try {
            val eventKey = eventModel.getEventKey(userid)
            val guestModel = GuestModel()

            guestList = guestModel.getGuests(userid, eventKey)
            db.execSQL("DELETE FROM GUEST")

            for (guestItem in guestList){
                insert(guestItem)
            }
        } catch (e: Exception){
            println(e.message)
            throw FirebaseDataImportException("Error importing Guest data: $e")
        }
        return true
    }

    fun insert(guest: Guest) {
        val values = ContentValues()
        values.put("guestid", guest.key)
        values.put("name", guest.name)
        values.put("phone", guest.phone)
        values.put("email", guest.email)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)
        db.insert("GUEST", null, values)
        Log.d(TAG, "Guest record inserted")
    }

    private fun getGuestexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST WHERE guestid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        cursor.close()
        return existsflag
    }

    @SuppressLint("Range")
    fun getAllGuests(): ArrayList<Guest> {
        val list = ArrayList<Guest>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST ORDER BY name ASC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val guestid = cursor.getString(cursor.getColumnIndex("guestid"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val phone = cursor.getString(cursor.getColumnIndex("phone"))
                    val email = cursor.getString(cursor.getColumnIndex("email"))
                    val rsvp = cursor.getString(cursor.getColumnIndex("rsvp"))
                    val companion = cursor.getString(cursor.getColumnIndex("companion"))
                    val table = cursor.getString(cursor.getColumnIndex("tableguest"))
                    val guest =
                        Guest(guestid, name, phone, email, rsvp, companion, table)
                    list.add(guest)
                    Log.d(TAG, "Guest $guestid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun update(guest: Guest) {
        val values = ContentValues()
        values.put("guestid", guest.key)
        values.put("name", guest.name)
        values.put("phone", guest.phone)
        values.put("email", guest.email)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)

        val retVal = db.update("GUEST", values, "guestid = '${guest.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Guest ${guest.key} updated")
        } else {
            Log.d(TAG, "Guest ${guest.key} not updated")
        }
        //db.close()
    }

    fun delete(guest: Guest) {
        val retVal = db.delete("GUEST", "guestid = '${guest.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Guest ${guest.key} deleted")
        } else {
            Log.d(TAG, "Guest ${guest.key} not deleted")
        }
        //db.close()
    }

    override fun onAddEditGuest(guest: Guest) {
        if (!getGuestexists(guest.key)) {
            insert(guest)
        } else {
            update(guest)
        }
        nexthandler?.onAddEditGuest(guest)
    }

    override fun onDeleteGuest(guest: Guest) {
        delete(guest)
        nexthandlerdel?.onDeleteGuest(guest)
    }

    companion object {
        const val TAG = "GuestDBHelper"
    }
}

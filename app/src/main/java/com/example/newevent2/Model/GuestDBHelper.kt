package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.getUserSession
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class GuestDBHelper(context: Context) : CoRAddEditGuest, CoRDeleteGuest {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var guest: Guest
    var key = ""
    var nexthandler: CoRAddEditGuest? = null
    var nexthandlerdel: CoRDeleteGuest? = null

    fun insert(guest: Guest) {
        var values = ContentValues()
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

    fun getGuestexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST WHERE guestid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        return existsflag
    }

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
        return list
    }

    fun getGuestsbyRSVP(rsvp: String): ArrayList<Guest> {
        val list = ArrayList<Guest>()
        val cursor: Cursor =
            db.rawQuery("SELECT * FROM GUEST WHERE rsvp ='$rsvp' ORDER BY name ASC", null)
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
                    val tableguest = cursor.getString(cursor.getColumnIndex("tableguest"))
                    val guest =
                        Guest(name, phone, email, rsvp, companion, tableguest)
                    list.add(guest)
                    Log.d(TAG, "Guest $guestid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun getGuestsStats(): GuestStatsToken {
        var gueststats = GuestStatsToken()

        val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST ORDER BY name ASC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    when (cursor.getString(cursor.getColumnIndex("rsvp"))) {
                        "y" -> gueststats.confirmed += 1
                        "n" -> gueststats.rejected += 1
                        "pending" -> gueststats.pending += 1
                    }
                    Log.d(
                        TAG,
                        "Guest stats obtained from local DB. Confirmed (${gueststats.confirmed}), Rejected (${gueststats.rejected}), Pending(${gueststats.pending})"
                    )
                } while (cursor.moveToNext())
            }
        }
        return gueststats
    }

    fun getGuestsperTable(): ArrayList<TableGuests> {
        var tablelist: ArrayList<String> = ArrayList()
        val cursor: Cursor = db.rawQuery("SELECT DISTINCT tableguest FROM GUEST", null)

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    tablelist.add(cursor.getString(cursor.getColumnIndex("tableguest")))
                } while (cursor.moveToNext())
            }
        }

        var guesttables: ArrayList<TableGuests> = ArrayList()
        var guesttableslist: ArrayList<Guest> = ArrayList()

        for (tables in tablelist) {
            guesttableslist.clear()
            var count = 0
            for (guest in guesttableslist) {
                if (guest.table == tables) {
                    count += (guest.companion.toInt() + 1) //adding the Guest itself
                    guesttableslist.add(guest)
                }
            }
            val newguestlist: ArrayList<Guest> = ArrayList(guesttableslist.size)
            for (guestlist in guesttableslist) newguestlist.add(guestlist)
            guesttables.add(TableGuests(tables, count, newguestlist))

        }
        return guesttables
    }

    fun update(guest: Guest) {
        var values = ContentValues()
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
        db.close()
    }

    fun delete(guest: Guest) {
        val retVal = db.delete("GUEST", "guestid = '${guest.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Guest ${guest.key} deleted")
        } else {
            Log.d(TAG, "Guest ${guest.key} not deleted")
        }
        db.close()
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

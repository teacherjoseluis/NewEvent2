package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.Functions.CoRAddEditGuest
import com.bridesandgrooms.event.Functions.CoRDeleteGuest
import kotlinx.coroutines.ExperimentalCoroutinesApi

class GuestDBHelper() : CoRAddEditGuest, CoRDeleteGuest {

    lateinit var guest: Guest
    var nexthandler: CoRAddEditGuest? = null
    var nexthandlerdel: CoRDeleteGuest? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val guestList: ArrayList<Guest>
        //val eventModel = EventModel()
        try {
            //val eventKey = eventModel.getEventKey(uid)
            val guestModel = GuestModel()

            guestList = guestModel.getGuests()
            db.execSQL("DELETE FROM GUEST")

            for (guestItem in guestList){
                insert(guestItem)
            }
        } catch (e: Exception){
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Guest data: $e")
//        }
//        finally {
//            db.close()
        }
        return true
    }

    fun insert(guest: Guest) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("guestid", guest.key)
        values.put("name", guest.name)
        values.put("phone", guest.phone)
        values.put("email", guest.email)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)

        val guestExists = getGuestexists(guest.key)

        try {
            if (!guestExists) {
                db.insert("GUEST", null, values)
                Log.d(TAG, "Guest record inserted")
            } else {
                Log.d(TAG, "Guest with ID ${guest.key} already exists, skipping insertion")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        }
//        finally {
//            db.close()
        }
    }


    private fun getGuestexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST WHERE guestid = '$key'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
            return existsflag
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return false
//        }
//        finally {
//            db.close()
        }
    }

    @SuppressLint("Range")
    fun getAllGuests(): ArrayList<Guest>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val list = ArrayList<Guest>()
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM GUEST ORDER BY name ASC", null)
            if (cursor.moveToFirst()) {
//            if (cursor.count > 0) {
//                cursor.moveToFirst()
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
//            }
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getNumberGuests(): Int? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var guestCount = 0
        try {
            val cursor: Cursor = db.rawQuery("SELECT count(*) as guestcount FROM GUEST WHERE rsvp <> 'n'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                guestCount = cursor.getString(cursor.getColumnIndexOrThrow("guestcount")).toInt()
                Log.d(TAG, "Guest count obtained from local DB")
            }
            cursor.close()
            return guestCount
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun update(guest: Guest) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val values = ContentValues()
        values.put("guestid", guest.key)
        values.put("name", guest.name)
        values.put("phone", guest.phone)
        values.put("email", guest.email)
        values.put("rsvp", guest.rsvp)
        values.put("companion", guest.companion)
        values.put("tableguest", guest.table)

        try {
            val retVal = db.update("GUEST", values, "guestid = '${guest.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Guest ${guest.key} updated")
            } else {
                Log.d(TAG, "Guest ${guest.key} not updated")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        }
//        finally {
//            db.close()
        }
    }

    fun delete(guestId: String) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        try {
            val retVal = db.delete("GUEST", "guestid = '$guestId'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Guest $guestId deleted")
            } else {
                Log.d(TAG, "Guest $guestId not deleted")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        }
//        finally {
//            db.close()
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
        Log.i("GuestDBHelper", "onAddEditGuest reached")
    }

    override fun onDeleteGuest(guestId: String) {
        delete(guestId)
        nexthandlerdel?.onDeleteGuest(guestId)
    }

    companion object {
        const val TAG = "GuestDBHelper"
    }
}

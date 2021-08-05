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

class VendorDBHelper(context: Context) : CoRAddEditVendor, CoRDeleteVendor {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var vendor: Vendor
    var key = ""
    var nexthandler: CoRAddEditVendor? = null
    var nexthandlerdel: CoRDeleteVendor? = null

    fun insert(vendor: Vendor) {
        var values = ContentValues()
        values.put("vendorid", vendor.key)
        values.put("name", vendor.name)
        values.put("phone", vendor.phone)
        values.put("email", vendor.email)
        values.put("category", vendor.category)
        values.put("eventid", vendor.eventid)
        values.put("placeid", vendor.placeid)
        values.put("location", vendor.location)
        db.insert("VENDOR", null, values)
        Log.d(TAG, "Vendor record inserted")
    }

    fun getVendorexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR WHERE vendorid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        return existsflag
    }

    fun getAllVendors(): ArrayList<Vendor> {
        val list = ArrayList<Vendor>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR ORDER BY name ASC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
                    val name=cursor.getString(cursor.getColumnIndex("name"))
                    val phone=cursor.getString(cursor.getColumnIndex("phone"))
                    val email=cursor.getString(cursor.getColumnIndex("email"))
                    val category=cursor.getString(cursor.getColumnIndex("category"))
                    val eventid=cursor.getString(cursor.getColumnIndex("eventid"))
                    val placeid=cursor.getString(cursor.getColumnIndex("placeid"))
                    val location=cursor.getString(cursor.getColumnIndex("location"))

                    val vendor =
                        Vendor(vendorid, name, phone, email, category, eventid, placeid, location)
                    list.add(vendor)
                    Log.d(TAG, "Vendor $vendorid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun update(vendor: Vendor) {
        var values = ContentValues()
        values.put("vendorid", vendor.key)
        values.put("name", vendor.name)
        values.put("phone", vendor.phone)
        values.put("email", vendor.email)
        values.put("category", vendor.category)
        values.put("eventid", vendor.eventid)
        values.put("placeid", vendor.placeid)
        values.put("location", vendor.location)

        val retVal = db.update("VENDOR", values, "vendorid = '${vendor.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Vendor ${vendor.key} updated")
        } else {
            Log.d(TAG, "Vendor ${vendor.key} not updated")
        }
        db.close()
    }

    fun delete(vendor: Vendor) {
        val retVal = db.delete("VENDOR", "vendorid = '${vendor.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Vendor ${vendor.key} deleted")
        } else {
            Log.d(TAG, "Vendor ${vendor.key} not deleted")
        }
        db.close()
    }

    override fun onAddEditVendor(vendor: Vendor) {
        if (!getVendorexists(vendor.key)) {
            insert(vendor)
        } else {
            update(vendor)
        }
        nexthandler?.onAddEditVendor(vendor)
    }

    override fun onDeleteVendor(vendor: Vendor) {
        delete(vendor)
        nexthandlerdel?.onDeleteVendor(vendor)
    }

    companion object {
        const val TAG = "VendorDBHelper"
    }
}

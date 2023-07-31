package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.collections.ArrayList

class VendorDBHelper(context: Context) : CoRAddEditVendor, CoRDeleteVendor {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var vendor: Vendor
    var key = ""
    var nexthandler: CoRAddEditVendor? = null
    var nexthandlerdel: CoRDeleteVendor? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String) : Boolean {
        val vendorList: ArrayList<Vendor>
        val eventModel = EventModel()
        try {
            val eventKey = eventModel.getEventKey(userid)
            val vendorModel = VendorModel()

            vendorList = vendorModel.getVendors(userid, eventKey)
            db.execSQL("DELETE FROM VENDOR")

            for (vendorItem in vendorList){
                insert(vendorItem)
            }
        } catch (e: Exception){
            println(e.message)
            throw FirebaseDataImportException("Error importing Vendor data: $e")
        }
        return true
    }

    fun insert(vendor: Vendor) {
        val values = ContentValues()
        values.put("vendorid", vendor.key)
        values.put("name", vendor.name)
        values.put("phone", vendor.phone)
        values.put("email", vendor.email)
        values.put("category", vendor.category)
        values.put("eventid", vendor.eventid)
        values.put("placeid", vendor.placeid)
        values.put("location", vendor.location)
        values.put("googlevendorname", vendor.googlevendorname)
        values.put("ratingnumber", vendor.ratingnumber)
        values.put("reviews", vendor.reviews)
        values.put("rating", vendor.rating)
        db.insert("VENDOR", null, values)
        Log.d(TAG, "Vendor record inserted")
    }

    private fun getVendorexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR WHERE vendorid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        cursor.close()
        return existsflag
    }

    @SuppressLint("Range")
    fun getVendorIdByName(name: String): String {
        var vendorid = ""
        val cursor: Cursor =
            db.rawQuery("SELECT vendorid FROM VENDOR WHERE name = '$name' LIMIT 1", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
            }
        }
        cursor.close()
        return vendorid
    }

    @SuppressLint("Range")
    fun getVendorNameById(id: String): String {
        var name = ""
        val cursor: Cursor =
            db.rawQuery("SELECT name FROM VENDOR WHERE vendorid = '$id' LIMIT 1", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                name = cursor.getString(cursor.getColumnIndex("name"))
            }
        }
        cursor.close()
        return name
    }

    @SuppressLint("Range")
    fun getAllVendors(): ArrayList<Vendor> {
        val list = ArrayList<Vendor>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR ORDER BY name ASC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val phone = cursor.getString(cursor.getColumnIndex("phone"))
                    val email = cursor.getString(cursor.getColumnIndex("email"))
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val placeid = cursor.getString(cursor.getColumnIndex("placeid"))
                    val location = cursor.getString(cursor.getColumnIndex("location"))
                    val googlevendorname = cursor.getString(cursor.getColumnIndex("googlevendorname"))
                    val ratingnumber = cursor.getFloat(cursor.getColumnIndex("ratingnumber"))
                    val reviews = cursor.getFloat(cursor.getColumnIndex("reviews"))
                    val rating = cursor.getString(cursor.getColumnIndex("rating"))

                    val vendor =
                        Vendor(vendorid, name, phone, email, category, eventid, placeid, location, googlevendorname, ratingnumber, reviews, rating)
                    list.add(vendor)
                    Log.d(TAG, "Vendor $vendorid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun update(vendor: Vendor) {
        val values = ContentValues()
        values.put("vendorid", vendor.key)
        values.put("name", vendor.name)
        values.put("phone", vendor.phone)
        values.put("email", vendor.email)
        values.put("category", vendor.category)
        values.put("eventid", vendor.eventid)
        values.put("placeid", vendor.placeid)
        values.put("location", vendor.location)
        values.put("googlevendorname", vendor.googlevendorname)
        values.put("ratingnumber", vendor.ratingnumber)
        values.put("reviews", vendor.reviews)
        values.put("rating", vendor.rating)

        val retVal = db.update("VENDOR", values, "vendorid = '${vendor.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Vendor ${vendor.key} updated")
        } else {
            Log.d(TAG, "Vendor ${vendor.key} not updated")
        }
        //db.close()
    }

    fun delete(vendor: Vendor) {
        val retVal = db.delete("VENDOR", "vendorid = '${vendor.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Vendor ${vendor.key} deleted")
        } else {
            Log.d(TAG, "Vendor ${vendor.key} not deleted")
        }
        //db.close()
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

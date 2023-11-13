package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.*
import com.bridesandgrooms.event.Functions.CoRAddEditVendor
import com.bridesandgrooms.event.Functions.CoRDeleteVendor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.collections.ArrayList

class VendorDBHelper(val context: Context) : CoRAddEditVendor, CoRDeleteVendor{

    lateinit var vendor: Vendor
    var key = ""
    var nexthandler: CoRAddEditVendor? = null
    var nexthandlerdel: CoRDeleteVendor? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String) : Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Vendor data: $e")
        } finally {
            db.close()
        }
        return true
    }

    fun insert(vendor: Vendor) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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

        val vendorExists = getVendorexists(vendor.key)

        try {
            if (!vendorExists) {
                db.insert("VENDOR", null, values)
                Log.d(TAG, "Vendor record inserted")
            } else {
                Log.d(TAG, "Vendor with ID ${vendor.key} already exists, skipping insertion")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }


    private fun getVendorexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR WHERE vendorid = '$key'", null)
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
    fun getVendorIdByName(name: String): String? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var vendorid = ""
        try {
            val cursor: Cursor =
                db.rawQuery("SELECT vendorid FROM VENDOR WHERE name = '$name' LIMIT 1", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                vendorid = cursor.getString(cursor.getColumnIndex("vendorid"))
            }
            cursor.close()
            return vendorid
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    @SuppressLint("Range")
    fun getVendorNameById(id: String): String? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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
    fun getAllVendors(): ArrayList<Vendor>? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val list = ArrayList<Vendor>()
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM VENDOR ORDER BY name ASC", null)
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
                    val googlevendorname =
                        cursor.getString(cursor.getColumnIndex("googlevendorname"))
                    val ratingnumber = cursor.getFloat(cursor.getColumnIndex("ratingnumber"))
                    val reviews = cursor.getFloat(cursor.getColumnIndex("reviews"))
                    val rating = cursor.getString(cursor.getColumnIndex("rating"))

                    val vendor =
                        Vendor(
                            vendorid,
                            name,
                            phone,
                            email,
                            category,
                            eventid,
                            placeid,
                            location,
                            googlevendorname,
                            ratingnumber,
                            reviews,
                            rating
                        )
                    list.add(vendor)
                    Log.d(TAG, "Vendor $vendorid record obtained from local DB")
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun update(vendor: Vendor) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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

        try {
            val retVal = db.update("VENDOR", values, "vendorid = '${vendor.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Vendor ${vendor.key} updated")
            } else {
                Log.d(TAG, "Vendor ${vendor.key} not updated")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
        //db.close()
    }

    fun delete(vendor: Vendor) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        try {
            val retVal = db.delete("VENDOR", "vendorid = '${vendor.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Vendor ${vendor.key} deleted")
            } else {
                Log.d(TAG, "Vendor ${vendor.key} not deleted")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
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

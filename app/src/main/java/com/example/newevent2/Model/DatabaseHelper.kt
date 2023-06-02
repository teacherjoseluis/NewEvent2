package com.example.newevent2.Model

import Application.FirebaseDataImportException
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

class DatabaseHelper(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASENAME, null,
    DATABASEVERSION
) {

    private val createusertable =
        "CREATE TABLE USER (userid TEXT, eventid TEXT, shortname TEXT, email TEXT, country TEXT, language TEXT, createdatetime TEXT, authtype TEXT, imageurl TEXT, role TEXT, hasevent TEXT, hastask TEXT, haspayment TEXT, hasguest TEXT, hasvendor TEXT, tasksactive INTEGER, taskscompleted INTEGER, payments INTEGER, guests INTEGER, status TEXT, vendors TEXT)"
    private val createtasktable =
        "CREATE TABLE TASK (taskid TEXT, name TEXT, date TEXT, category TEXT, budget TEXT, status TEXT, eventid TEXT, createdatetime TEXT)"
    private val createpaymenttable =
        "CREATE TABLE PAYMENT (paymentid TEXT, name TEXT, date TEXT, category TEXT, amount TEXT, eventid TEXT, createdatetime TEXT, vendorid TEXT)"
    private val createeventtable =
        "CREATE TABLE EVENT (eventid TEXT, imageurl TEXT, placeid TEXT, latitude REAL, longitude REAL, address TEXT, name TEXT, date TEXT, time TEXT, about TEXT, location TEXT)"
    private val createguesttable =
        "CREATE TABLE GUEST (guestid TEXT, name TEXT, phone TEXT, email TEXT, rsvp TEXT, companion TEXT, tableguest TEXT)"
    private val createvendortable =
        "CREATE TABLE VENDOR (vendorid TEXT, name TEXT, phone TEXT, email TEXT, category TEXT, eventid TEXT, placeid TEXT, location TEXT, googlevendorname TEXT, ratingnumber FLOAT, reviews FLOAT, rating TEXT)"
    private val createnotetable =
        "CREATE TABLE NOTE (noteid INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, body TEXT, color TEXT, lastupdateddatetime TEXT)"

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL(createusertable)
        p0.execSQL(createtasktable)
        p0.execSQL(createpaymenttable)
        p0.execSQL(createeventtable)
        p0.execSQL(createguesttable)
        p0.execSQL(createvendortable)
        p0.execSQL(createnotetable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS TASK")
        p0.execSQL("DROP TABLE IF EXISTS PAYMENT")
        p0.execSQL("DROP TABLE IF EXISTS EVENT")
        p0.execSQL("DROP TABLE IF EXISTS GUEST")
        p0.execSQL("DROP TABLE IF EXISTS VENDOR")
        p0.execSQL("DROP TABLE IF EXISTS NOTE")
        onCreate(p0)
    }

    @ExperimentalCoroutinesApi
    suspend fun updateLocalDB(userid : String) : Boolean {
        val userDB = UserDBHelper(context)
        val eventDB = EventDBHelper(context)
        val taskDB = TaskDBHelper(context)
        val paymentDB = PaymentDBHelper(context)
        val guestDB = GuestDBHelper(context)
        val vendorDB = VendorDBHelper(context)
        try {
            userDB.firebaseImport(userid)
            eventDB.firebaseImport(userid)
            taskDB.firebaseImport(userid)
            paymentDB.firebaseImport(userid)
            guestDB.firebaseImport(userid)
            vendorDB.firebaseImport(userid)
        } catch (e: FirebaseDataImportException){
            println(e.message)
        }
        return true
    }

    companion object {
        private const val DATABASENAME = "BDCACHE"
        private const val DATABASEVERSION = 11
    }
}

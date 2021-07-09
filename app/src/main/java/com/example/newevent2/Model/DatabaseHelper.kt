package com.example.newevent2.Model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASENAME, null,
    DATABASEVERSION
) {
    private val createtasktable =
        "CREATE TABLE TASK (taskid TEXT, name TEXT, date TEXT, category TEXT, budget TEXT, status TEXT, eventid TEXT, createdatetime TEXT)"
    private val createpaymenttable =
        "CREATE TABLE PAYMENT (paymentid TEXT, name TEXT, date TEXT, category TEXT, amount TEXT, eventid TEXT, createdatetime TEXT)"
    private val createeventtable =
        "CREATE TABLE EVENT (eventid TEXT, imageurl TEXT, placeid TEXT, latitude REAL, longitude REAL, address TEXT, name TEXT, date TEXT, time TEXT, about TEXT, location TEXT)"
    private val createguesttable =
        "CREATE TABLE GUEST (guestid TEXT, name TEXT, phone TEXT, email TEXT, rsvp TEXT, companion TEXT, tableguest TEXT)"

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL(createtasktable)
        p0.execSQL(createpaymenttable)
        p0.execSQL(createeventtable)
        p0.execSQL(createguesttable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS TASK")
        p0.execSQL("DROP TABLE IF EXISTS PAYMENT")
        p0.execSQL("DROP TABLE IF EXISTS EVENT")
        p0.execSQL("DROP TABLE IF EXISTS GUEST")
        onCreate(p0)
    }

    companion object {
        private const val DATABASENAME = "BDCACHE"
        private const val DATABASEVERSION = 5
    }
}
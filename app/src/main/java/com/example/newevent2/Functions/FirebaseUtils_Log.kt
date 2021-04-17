package com.example.newevent2.Functions

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
private var myRef = database.reference

internal fun saveLog(
    action: String,
    entity: String,
    entityid: String,
    entityname: String,
    userid: String,
    eventid: String
) {
    val postRef =
        myRef.child("User").child(userid).child("Event").child(eventid)
            .child("Log").push()

    //---------------------------------------
    // Getting the time and date to record in the recently created task
    val timestamp = Time(System.currentTimeMillis())
    val taskdatetime = Date(timestamp.time)
    val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
    val nodedate = SimpleDateFormat("yyyyMMdd")
    //---------------------------------------

    val loginfo = hashMapOf(
        "userid" to userid,
        //"eventid" to usersessionlist[3],
        "action" to action,
        "entity" to entity,
        "entityid" to entityid,
        "entityname" to entityname,
        "date" to nodedate.format(taskdatetime),
        "createdatetime" to sdf.format(taskdatetime)
    )

    postRef.setValue(loginfo as Map<String, Any>)
        .addOnFailureListener {
        }
        .addOnSuccessListener {
        }
}

internal fun getLog(
    userid: String,
    eventid: String,
    dataFetched: FirebaseGetLogSuccess
) {
    val postRef =
        myRef.child("User").child(userid).child("Event").child(eventid)
            .child("Log")
            .orderByChild("date")
    var loginfolist = ArrayList<Loginfo>()
    val logListenerActive = object : ValueEventListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDataChange(p0: DataSnapshot) {
            for (snapshot in p0.children) {
                val loginfoitem = snapshot.getValue(Loginfo::class.java)!!
                loginfoitem.logkey = snapshot.key.toString()
                loginfolist.add(loginfoitem)
            }
            dataFetched.onGetLogSuccess(loginfolist)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("loadPost:onCancelled ${databaseError.toException()}")
        }
    }
    postRef.addValueEventListener(logListenerActive)
}

internal fun removeLog(
    userid: String,
    eventid: String,
    logkey: String
) {
    val delRef =
        myRef.child("User").child(userid).child("Event").child(eventid)
            .child("Log").child(logkey)
    delRef.removeValue()
}


class Loginfo {
    var logkey: String = ""
    var userid: String = ""
    var action: String = ""
    var entity: String = ""
    var entityid: String = ""
    var entityname: String = ""
    var date: String = ""
    var createdatetime: String = ""
}

interface FirebaseGetLogSuccess {
    fun onGetLogSuccess(loglist: ArrayList<Loginfo>)
}
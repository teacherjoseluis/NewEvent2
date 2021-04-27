package com.example.newevent2.Model

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerEvent
import com.example.newevent2.Functions.getCurrentDateTime
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.saveLog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EventModel() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    var userid = ""

    fun addEvent(event: Event, uri: Uri?, savesuccessflag: EventModel.FirebaseSaveSuccess) {

        val postRef = myRef.child("User").child(userid).child("Event").push()
        val eventmap = hashMapOf(
            "imageurl" to event.imageurl,
            "placeid" to event.placeid,
            "latitude" to event.latitude,
            "longitude" to event.longitude,
            "address" to event.address,
            "name" to event.name,
            "date" to event.date,
            "time" to event.time,
            "about" to event.about,
            "location" to event.location
        )

        postRef.setValue(
            eventmap as Map<String, Any>
        ) { error, _ ->
            if (error != null) {
                //Se loggea un error al guardar el usuario TODO databaseError.getMessage()
                savesuccessflag.onSaveSuccess("")
            } else {
//                // Saving in the log the new creation of the event
                val eventid = postRef.key.toString()
//                saveLog(context, "INSERT", "event", eventid, event.name, userid, eventid)
                //Save Event image in Storage
                if (uri != null) {
                    val imageRef =
                        storageRef.child("User/$userid/Event/${eventid}/images/${uri.lastPathSegment}")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        return@addOnFailureListener
                    }.addOnSuccessListener {
                        return@addOnSuccessListener
                    }
                }
                savesuccessflag.onSaveSuccess(postRef.key.toString())
            }
        }
    }

    fun getEventdetail(userid: String, eventid: String, dataFetched: FirebaseSuccessListenerEventDetail) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)

        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val eventitem = p0.getValue(Event::class.java)!!
                eventitem!!.key = p0.key.toString()
                dataFetched.onEvent(eventitem)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListenerActive)
    }

    interface FirebaseSaveSuccess {
        fun onSaveSuccess(eventid: String)
    }

    interface FirebaseSuccessListenerEventDetail {
        fun onEvent(event: Event)
    }
}
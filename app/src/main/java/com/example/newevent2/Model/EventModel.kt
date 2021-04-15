package com.example.newevent2.Model

import android.content.Context
import android.net.Uri
import com.example.newevent2.Functions.getCurrentDateTime
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.saveLog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EventModel(context: Context) {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    var userid = ""
    private val context = context

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
                // Saving in the log the new creation of the event
                val eventid = postRef.key.toString()
                saveLog(context, "INSERT", "event", eventid, event.name, userid, eventid)
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

    interface FirebaseSaveSuccess {
        fun onSaveSuccess(eventid: String)
    }
}
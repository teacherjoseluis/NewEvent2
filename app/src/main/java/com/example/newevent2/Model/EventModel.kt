package com.example.newevent2.Model

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.CoRAddEditEvent
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Functions.saveImgtoStorage
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.MVP.ImagePresenter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class EventModel : CoRAddEditEvent {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandlere: CoRAddEditEvent? = null

    var userid = ""
    lateinit var event: Event

    //Need to convert this one into a suspend function
    private suspend fun addEvent(
        //userid: String,
        event: Event,
        uri: Uri?
        //savesuccessflag: FirebaseSaveSuccess
    ) {
        coroutineScope {
            val postRef = myRef.child("User").child(this@EventModel.userid).child("Event").push()
            val eventmap = hashMapOf(
                "imageurl" to event.imageurl,
                "placeid" to event.placeid,
                "latitude" to event.latitude,
                "longitude" to event.longitude,
                "address" to event.address,
                "name" to event.name,
                "date" to event.date,
                "time" to event.time,
                "about" to event.eventid,
                "location" to event.location
            )

            postRef.setValue(
                eventmap as Map<String, Any>
            ).await()
            val eventid = postRef.key.toString()
            //Save Event image in Storage
            if (uri != null) {
                saveImgtoStorage(ImagePresenter.EVENTIMAGE, userid, eventid, uri)
            }
//            { error, _ ->
//                if (error != null) {
//                    //Se loggea un error al guardar el usuario TODO databaseError.getMessage()
//                    savesuccessflag.onSaveSuccess("")
//                } else {
//                    // Saving in the log the new creation of the event
//                    val eventid = postRef.key.toString()
//                    //Save Event image in Storage
//                    if (uri != null) {
//                        saveImgtoStorage(ImagePresenter.EVENTIMAGE, userid, eventid, uri)
//                        savesuccessflag.onSaveSuccess(postRef.key.toString())
//                    }
//                }
//            }
        }
    }

    fun editEvent(
        userid: String,
        event: Event,
        savesuccessflag: FirebaseSaveSuccess
    ) {

        val postRef =
            myRef.child("User").child(userid).child("Event").child(event.key)

        postRef.child("name").setValue(event.name)
        postRef.child("date").setValue(event.date)
        postRef.child("time").setValue(event.time)
        postRef.child("location").setValue(event.location)
        postRef.child("placeid").setValue(event.placeid)
        postRef.child("latitude").setValue(event.latitude)
        postRef.child("longitude").setValue(event.longitude)
        postRef.child("address").setValue(event.address)

        savesuccessflag.onSaveSuccess(event.key)
    }

    fun getEventdetail(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessListenerEventDetail
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)

        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val eventitem = p0.getValue(Event::class.java)!!
                eventitem.key = p0.key.toString()
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

    interface FirebaseSuccessListenerEventKey {
        fun onEvent(eventkey: String)
    }

    override suspend fun onAddEditEvent(event: Event) {
        addEvent(event, null)
        nexthandlere?.onAddEditEvent(event)
    }
}

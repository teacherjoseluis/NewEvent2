package com.bridesandgrooms.event.Model

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.CoRAddEditEvent
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.saveImgtoStorage
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EventModel : CoRAddEditEvent, CoROnboardUser {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandlere: CoRAddEditEvent? = null
    var nexthandleron: CoROnboardUser? = null

    lateinit var event: Event

    //Need to convert this one into a suspend function
    private suspend fun addEvent(
        user: User,
        event: Event,
        uri: Uri?
        //savesuccessflag: FirebaseSaveSuccess
    ): String {
//        coroutineScope {
        val postRef = myRef.child("User").child(user.userid!!).child("Event").push()
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
        val userEventIdRef =
            myRef.child("User").child(user.userid!!).child("eventid")
        userEventIdRef.setValue(eventid).await()

        //Save Event image in Storage
        if (uri != null) {
            saveImgtoStorage(ImagePresenter.EVENTIMAGE, user.userid!!, eventid, uri)
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
//        }
        return eventid
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

    @ExperimentalCoroutinesApi
    suspend fun getEventKey(userid: String): String {
        var eventkey = ""
        val postRef =
            myRef.child("User").child(userid).child("Event").limitToFirst(1)
        return try {
            for (snapchild in postRef.awaitsSingle()?.children!!) {
                eventkey = snapchild.key.toString()
            }
            eventkey
        } catch (e: Exception) {
            Log.d(
                UserModel.TAG,
                "Data associated to User cannot ben retrieved from Firebase"
            )
            ""
        }
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


    @ExperimentalCoroutinesApi
    suspend fun getEvent(userid: String): Event {
        val postRef =
            myRef.child("User").child(userid).child("Event").limitToFirst(1)
        var eventItem: Event? = null
        try {
            for (snapChild in postRef.awaitsSingle()?.children!!) {
                eventItem = snapChild.getValue(Event::class.java)
                eventItem!!.key = snapChild.key.toString()
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return eventItem!!
    }

//    @ExperimentalCoroutinesApi
//    suspend fun getEventChildrenflag(
//        userid: String,
//        eventid: String
//    ): Boolean {
//        var existsflag = false
//        val postRef =
//            myRef.child("User").child(userid).child("Event").child(eventid)
//        try {
//            existsflag = when {
//                postRef.awaitsSingle()?.hasChild("Task") == true -> true
//                postRef.awaitsSingle()?.hasChild("Payment") == true -> true
//                else -> false
//            }
//        } catch (e: Exception) {
//            Log.d(
//                TAG,
//                "Data associated to Event cannot ben retrieved from Firebase"
//            )
//            false
//        }
//        return existsflag
//    }

    @ExperimentalCoroutinesApi
    suspend fun Query.awaitsSingle(): DataSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        continuation.resume(snapshot)
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = when (error.toException()) {
                        is FirebaseException -> error.toException()
                        else -> Exception("The Firebase call for reference $this was cancelled")
                    }
                    continuation.resumeWithException(exception)
                }
            }
            continuation.invokeOnCancellation { this.removeEventListener(listener) }
            this.addListenerForSingleValueEvent(listener)
        }

    interface FirebaseSaveSuccess {
        fun onSaveSuccess(eventid: String)
    }

    interface FirebaseSuccessListenerEventDetail {
        fun onEvent(event: Event)
    }

    override suspend fun onAddEditEvent(event: Event) {
        //addEvent(event, null)
        nexthandlere?.onAddEditEvent(event)
    }

    override suspend fun onOnboardUser(user: User, event: Event) {
        event.key = addEvent(user, event, null)
        nexthandleron?.onOnboardUser(user, event)
    }

    companion object {
        const val TAG = "EventModel"
    }
}

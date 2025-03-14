package com.bridesandgrooms.event.Model

import Application.EventCreationException
import Application.UserAuthenticationException
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.CoRAddEditEvent
import com.bridesandgrooms.event.Functions.CoROnboardUser
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.bridesandgrooms.event.Functions.saveImgtoStorage
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EventModel : CoRAddEditEvent, CoROnboardUser {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandlere: CoRAddEditEvent? = null
    var nexthandleron: CoROnboardUser? = null

    val userId = UserSessionHelper.getUserSession("user_id") as String
    private val eventid = UserSessionHelper.getUserSession("event_id") as String

    var eventId = ""
    lateinit var event: Event

    //Need to convert this one into a suspend function
    private suspend fun addEvent(
        event: Event,
        uri: Uri?
        //savesuccessflag: FirebaseSaveSuccess
    ): String {
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
        val postRef = myRef.child("User").child(userId).child("Event").push()
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

        try {
            if (currentUserUID == userId) {
                postRef.setValue(
                    eventmap as Map<String, Any>
                ).await()
                eventId = postRef.key.toString()

                val userEventIdRef =
                    myRef.child("User").child(userId)
                userEventIdRef.child("eventid").setValue(eventId).await()
            } else {
                throw UserAuthenticationException("User ID does not match with the Auth User in Firebase")
            }
        } catch (e: Exception) {
            throw EventCreationException(e.toString())
        }

        //Save Event image in Storage
        if (uri != null) {
            saveImgtoStorage(ImagePresenter.EVENTIMAGE, uri)
        }
        return eventId
    }

    fun editEvent(
        event: Event,
        savesuccessflag: FirebaseSaveSuccess
    ) {
        val postRef =
            myRef.child("User").child(userId).child("Event").child(event.key)
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
        dataFetched: FirebaseSuccessListenerEventDetail
    ) {
        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventid)

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
    suspend fun getEvent(): Event {
        val postRef =
            myRef.child("User").child(userId).child("Event").limitToFirst(1)
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

    suspend fun checkNeedsRefresh(): Boolean {
        return try {
            val eventNeedsRefreshRef = myRef
                .child("User")
                .child(userId)
                .child("Event")
                .child(eventid)
                .child("eventNeedsRefresh")

            // Get the data from Firebase at the specific path and wait for the result.
            val dataSnapshot = eventNeedsRefreshRef.get().await()

            // Get the boolean value directly from the DataSnapshot.
            // Use getValue(Boolean::class.java) to get the value as a Boolean.
            // Use ?: false to provide a default value of false if the value is null.
            dataSnapshot.getValue(Boolean::class.java) ?: false
        } catch (e: Exception) {
            // Handle exceptions appropriately (e.g., logging, returning a default value).
            println("Error checking eventNeedsRefresh for user $userId, event $eventId: ${e.message}")
            false // Or throw the exception if it should propagate.
        }
    }

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
        event.key = addEvent(event, null)
        nexthandleron?.onOnboardUser(user, event)
    }

    companion object {
        const val TAG = "EventModel"
    }
}

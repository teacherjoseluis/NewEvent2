package com.bridesandgrooms.event.Model

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.*
import com.bridesandgrooms.event.Functions.CoRAddEditGuest
import com.bridesandgrooms.event.Functions.CoRDeleteGuest
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GuestModel : CoRAddEditGuest, CoRDeleteGuest {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef = database.reference
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    var nexthandler: CoRAddEditGuest? = null
    var nexthandlerdel: CoRDeleteGuest? = null

    var userid = ""
    var eventid = ""

    @ExperimentalCoroutinesApi
    suspend fun getGuests(userid: String, eventid: String): ArrayList<Guest> {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").orderByChild("name")
        val guestList = ArrayList<Guest>()

        try {
            for (snapChild in postRef.awaitsSingle()?.children!!) {
                val guestItem = snapChild.getValue(Guest::class.java)
                guestItem!!.key = snapChild.key.toString()
                guestList.add(guestItem)
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return guestList
    }

    fun getAllGuestList(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessGuestList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").orderByChild("name")
        val guestlist = ArrayList<Guest>()

        val guestlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                guestlist.clear()

                for (snapshot in p0.children) {
                    val guestitem = snapshot.getValue(Guest::class.java)
                    guestitem!!.key = snapshot.key.toString()
                    guestlist.add(guestitem)
                }
                Log.d(TAG, "Number of guests retrieved ${guestlist.count()}")
                dataFetched.onGuestList(guestlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(guestlListenerActive)
    }

    @SuppressLint("SimpleDateFormat")
    private fun addGuest(
        userid: String,
        eventid: String,
        guest: Guest
    ): String {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created guest
        val timestamp = Time(System.currentTimeMillis())
        val guestdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------

        val guestadd = hashMapOf(
            "name" to guest.name,
            "rsvp" to guest.rsvp,
            "companion" to guest.companion,
            "table" to guest.table,
            "phone" to guest.phone,
            "email" to guest.email,
            "createdatetime" to sdf.format(guestdatetime)

        )
        coroutineScope.launch {
            postRef.setValue(guestadd as Map<String, Any>)
                .await()
        }
        return postRef.key.toString()
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun addGuest(
//        userid: String,
//        eventid: String,
//        guest: Guest,
//        guestaddedflag: FirebaseAddEditGuestSuccess
//    ) {
//        val postRef =
//            myRef.child("User").child(userid).child("Event").child(eventid)
//                .child("Guest").push()
//
//        //---------------------------------------
//        // Getting the time and date to record in the recently created guest
//        val timestamp = Time(System.currentTimeMillis())
//        val guestdatetime = Date(timestamp.time)
//        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
//        //---------------------------------------
//
//        val guestadd = hashMapOf(
//            "name" to guest.name,
//            "rsvp" to guest.rsvp,
//            "companion" to guest.companion,
//            "table" to guest.table,
//            "phone" to guest.phone,
//            "email" to guest.email,
//            "createdatetime" to sdf.format(guestdatetime)
//
//        )
//
//        postRef.setValue(guestadd as Map<String, Any>)
//            .addOnSuccessListener {
//                guest.key = postRef.key.toString()
//                guestaddedflag.onGuestAddedEdited(true, guest)
//                Log.d(
//                    TAG,
//                    "Guest ${guest.name} successfully added on ${sdf.format(guestdatetime)}")
//            }
//            .addOnFailureListener {
//                guestaddedflag.onGuestAddedEdited(false, guest)
//                Log.e(TAG, "Guest ${guest.name} failed to be added")
//            }
//    }

    private fun editGuest(
        userid: String,
        eventid: String,
        guest: Guest
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").child(guest.key)

        val guestedit = hashMapOf(
            "name" to guest.name,
            "rsvp" to guest.rsvp,
            "companion" to guest.companion,
            "table" to guest.table,
            "phone" to guest.phone,
            "email" to guest.email
        )
        coroutineScope.launch {
            postRef.setValue(guestedit as Map<String, Any>)
                .await()
        }
    }

    private fun deleteGuest(
        userid: String,
        eventid: String,
        guest: Guest,
        guestdeletedflag: FirebaseDeleteGuestSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").child(guest.key)
                .removeValue()
                .addOnSuccessListener {
                    guestdeletedflag.onGuestDeleted(true, guest)
                    Log.d(TAG, "Guest ${guest.name} successfully deleted")
                }
                .addOnFailureListener {
                    guestdeletedflag.onGuestDeleted(false, guest)
                    Log.e(TAG, "Guest ${guest.name} failed to be deleted")
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

    override fun onAddEditGuest(guest: Guest) {
        if (guest.key == "") {
            guest.key = addGuest(userid, eventid, guest)
            nexthandler?.onAddEditGuest(guest)
        } else if (guest.key != "") {
            editGuest(userid, eventid, guest)
            nexthandler?.onAddEditGuest(guest)
        }
        Log.i("GuestModel", "onAddEditGuest reached")
    }

    override fun onDeleteGuest(guest: Guest) {
        deleteGuest(
            userid,
            eventid,
            guest,
            object : FirebaseDeleteGuestSuccess {
                override fun onGuestDeleted(flag: Boolean, guest: Guest) {
                    if (flag) {
                        nexthandlerdel?.onDeleteGuest(guest)
                    }
                }
            })
    }

    interface FirebaseDeleteGuestSuccess {
        fun onGuestDeleted(flag: Boolean, guest: Guest)
    }

    interface FirebaseSuccessGuestList {
        fun onGuestList(list: ArrayList<Guest>)
    }

    companion object {
        const val TAG = "GuestModel"
        const val ACTIVEFLAG = "Y"
        const val INACTIVEFLAG = "Y"
    }
}

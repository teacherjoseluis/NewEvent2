package com.example.newevent2.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class GuestModel() : CoRAddEditGuest, CoRDeleteGuest {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef = database.reference
    var nexthandler: CoRAddEditGuest? = null
    var nexthandlerdel: CoRDeleteGuest? = null

    var userid = ""
    var eventid = ""

    fun getAllGuestList(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessGuestList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").orderByChild("name")
        var guestlist = ArrayList<Guest>()

        val guestlListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                guestlist.clear()

                for (snapshot in p0.children) {
                    val guestitem = snapshot.getValue(Guest::class.java)
                    guestitem!!.key = snapshot.key.toString()
                    guestlist.add(guestitem!!)
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

    fun getGuestdetail(
        userid: String,
        eventid: String,
        guestid: String,
        dataFetched: FirebaseSuccessGuest
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").child(guestid)

        val guestListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val guestitem = p0.getValue(Guest::class.java)!!
                guestitem!!.key = p0.key.toString()
                Log.d(PaymentModel.TAG, "Detail retrieved for guest ${guestitem.key}")
                dataFetched.onGuest(guestitem)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(guestListenerActive)
    }

    fun addGuest(
        userid: String,
        eventid: String,
        guest: Guest,
        guestaddedflag: FirebaseAddEditGuestSuccess
    ) {
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

        postRef.setValue(guestadd as Map<String, Any>)
            .addOnSuccessListener {
                guest.key = postRef.key.toString()
                guestaddedflag.onGuestAddedEdited(true, guest)
                Log.d(
                    TAG,
                    "Guest ${guest.name} successfully added on ${sdf.format(guestdatetime)}")
            }
            .addOnFailureListener {

                guestaddedflag.onGuestAddedEdited(false, guest)
                Log.e(TAG, "Guest ${guest.name} failed to be added")
            }
    }

    fun editGuest(
        userid: String,
        eventid: String,
        guest: Guest,
        guesteditedflag: FirebaseAddEditGuestSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").child(guest.key)

        val paymentedit = hashMapOf(
            "name" to guest.name,
            "rsvp" to guest.rsvp,
            "companion" to guest.companion,
            "table" to guest.table,
            "phone" to guest.phone,
            "email" to guest.email
        )

        postRef.setValue(paymentedit as Map<String, Any>)
            .addOnSuccessListener {
                guesteditedflag.onGuestAddedEdited(true, guest)
                Log.d(PaymentModel.TAG, "Guest ${guest.name} successfully edited")
            }
            .addOnFailureListener {
                guesteditedflag.onGuestAddedEdited(false, guest)
                Log.e(PaymentModel.TAG, "Guest ${guest.name} failed to be edited")
            }
    }

    fun deleteGuest(
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
                    Log.d(PaymentModel.TAG, "Guest ${guest.name} successfully deleted")
                }
                .addOnFailureListener {
                    guestdeletedflag.onGuestDeleted(false, guest)
                    Log.e(PaymentModel.TAG, "Guest ${guest.name} failed to be deleted")
                }
    }

    override fun onAddEditGuest(guest: Guest) {
        if (guest.key == "") {
            addGuest(
                userid,
                eventid,
                guest,
                object : FirebaseAddEditGuestSuccess {
                    override fun onGuestAddedEdited(flag: Boolean, guest: Guest) {
                        if (flag) {
                            nexthandler?.onAddEditGuest(guest)
                        }
                    }
                })
        } else if (guest.key != "") {
            editGuest(
                userid, eventid, guest, object : FirebaseAddEditGuestSuccess {
                    override fun onGuestAddedEdited(flag: Boolean, guest: Guest) {
                        if (flag) {
                            nexthandler?.onAddEditGuest(guest)
                        }
                    }
                }
            )
        }
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

    interface FirebaseSuccessGuest {
        fun onGuest(guest: Guest)
    }

    interface FirebaseAddEditGuestSuccess {
        fun onGuestAddedEdited(flag: Boolean, guest: Guest)
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

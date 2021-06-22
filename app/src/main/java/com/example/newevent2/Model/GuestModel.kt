package com.example.newevent2.Model

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerGuest
import com.example.newevent2.getUserSession
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class GuestModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef = database.reference

    fun getGuestsEvent(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessGuestStats
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest")

        var confirmed = 0
        var rejected = 0
        var pending = 0

        val guestListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val guestitem = snapshot.getValue(Guest::class.java)!!
                    if (guestitem.eventid == eventid) {
                        when (guestitem.rsvp) {
                            "y" -> confirmed += 1
                            "n" -> rejected += 1
                            "pending" -> pending += 1
                        }
                    }
                }
                dataFetched.onGuestConfirmation(confirmed, rejected, pending)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(guestListenerActive)
    }

    fun getAllGuestList(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessGuestList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Guest").orderByChild("date")
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
                Log.d(TaskModel.TAG, "Number of guests retrieved ${guestlist.count()}")
                dataFetched.onGuestList(guestlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(guestlListenerActive)
    }

    interface FirebaseSuccessGuestStats {
        fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int)
    }

    interface FirebaseSuccessGuestList {
        fun onGuestList(list: ArrayList<Guest>)
    }
}

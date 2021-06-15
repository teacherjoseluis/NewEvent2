package com.example.newevent2.Model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerGuest
import com.example.newevent2.Guest
import com.example.newevent2.getUserSession
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    interface FirebaseSuccessGuestStats {
        fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int)
    }
}

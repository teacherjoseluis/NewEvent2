package com.example.newevent2

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GuestEntity : Guest() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef = database.reference
    //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Guest")

    fun getGuestsContacts(dataFetched: FirebaseSuccessListener) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Guest")
        var contactList = ArrayList<String>()
        val guestListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {
                    val guestitem = snapshot.getValue(Guest::class.java)!!
                    if (guestitem.eventid == eventid) {
                        contactList.add(guestitem.contactid)
                    }
                }
                dataFetched.onListCreated(contactList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(guestListenerActive)
    }


//    fun editGuest(action: String) {
//        if (action == "complete") {
//            myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key)
//                .child("status").setValue("C")
//        }
//        if (action == "active") {
//            myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key)
//                .child("status").setValue("A")
//        }
//    }
//
//    fun deleteGuest() {
//        myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key).removeValue()
//    }

    fun addGuest() {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Guest").push()
        val guest = hashMapOf(
            "eventid" to eventid,
            "contactid" to contactid,
            "rsvp" to "pending",
            "companion" to companion,
            "table" to table
        )

        postRef.setValue(guest as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }
}
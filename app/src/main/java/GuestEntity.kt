//package com.example.newevent2
//
//import android.content.Context
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import kotlinx.android.synthetic.main.task_editdetail.*
//
//class GuestEntity : Guest() {
//
//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private val myRef = database.reference
//    //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Guest")
//
//    fun getGuestsContacts(context: Context, dataFetched: FirebaseSuccessListenerGuest) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3]).child("Guest")
//        var guestList = ArrayList<Guest>()
//        val guestListenerActive = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//
//                for (snapshot in p0.children) {
//                    val guestitem = snapshot.getValue(Guest::class.java)!!
//                    if (guestitem.eventid == eventid) {
//                        guestitem.key = snapshot.key.toString()
//                        guestList.add(guestitem)
//
//                    }
//                }
//                dataFetched.onGuestList(guestList)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
//        postRef.addValueEventListener(guestListenerActive)
//    }
//
////    fun editGuest(action: String) {
////        if (action == "complete") {
////            myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key)
////                .child("status").setValue("C")
////        }
////        if (action == "active") {
////            myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key)
////                .child("status").setValue("A")
////        }
////    }
////
////    fun deleteGuest() {
////        myRef.child("User").child("Event").child(this.eventid).child("Guest").child(this.key).removeValue()
////    }
//
//    fun addGuest(context: Context) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3]).child("Guest").push()
//        val guest = hashMapOf(
//            "name" to name,
//            //"eventid" to eventid,
//            "contactid" to contactid,
//            "rsvp" to "pending",
//            "companion" to companion,
//            "table" to table,
//            "imageurl" to imageurl,
//            "phone" to phone,
//            "email" to email
//        )
//
//        postRef.setValue(guest as Map<String, Any>)
//            .addOnFailureListener {
//            }
//            .addOnSuccessListener {
//            }
//    }
//
//    fun deleteGuest(context: Context) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3]).child("Guest")
//            .removeValue()
//    }
//
//
//    fun editGuest(context: Context) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3]).child("Guest")
//        postRef.child("name").setValue(name)
//        postRef.child("rsvp").setValue(rsvp)
//        postRef.child("companion").setValue(companion)
//        postRef.child("table").setValue(table)
//        postRef.child("imageurl").setValue(imageurl)
//        postRef.child("phone").setValue(phone)
//        postRef.child("email").setValue(email)
//    }
//
//    fun getGuestsEvent(context: Context, dataFetched: FirebaseSuccessListenerGuest) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3]).child("Guest")
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Guest")
//
//        var confirmed = 0
//        var rejected = 0
//        var pending = 0
//
//        val guestListenerActive = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//
//                for (snapshot in p0.children) {
//                    val guestitem = snapshot.getValue(Guest::class.java)!!
//                    if (guestitem.eventid == eventid) {
//                        when (guestitem.rsvp) {
//                            "y" -> confirmed += 1
//                            "n" -> rejected += 1
//                            "pending" -> pending += 1
//                        }
//
//                    }
//                }
//                dataFetched.onGuestConfirmation(confirmed, rejected, pending)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
//        postRef.addValueEventListener(guestListenerActive)
//    }
//
//
//}

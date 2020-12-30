package com.example.newevent2

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class UserEntity : User() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun getUserexists(dataFetched: FirebaseSuccessListenerUser) {
        val postRef = myRef.child("User").child(this.key)
        val userListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                var userexists = true
                if (!p0.exists()) {
                    userexists = false
                }
                dataFetched.onUserexists(userexists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(userListenerActive)
    }

    fun addUser() {
        val postRef = myRef.child("User").child(this.key)

        //---------------------------------------
        // Getting the time and date to record in the recently created task
        val timestamp = Time(System.currentTimeMillis())
        val taskdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------

        val user = hashMapOf(
            "eventid" to eventid,
            "shortname" to shortname,
            "email" to email,
            "country" to country,
            "language" to language,
            "createdatetime" to sdf.format(taskdatetime),
            "status" to "A"
        )

        postRef.setValue(user as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }
}

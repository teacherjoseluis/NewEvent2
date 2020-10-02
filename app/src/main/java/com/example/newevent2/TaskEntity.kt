package com.example.newevent2

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class TaskEntity : Task() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun editTask(action: String) {
        if (action == "complete") {
            myRef.child("User").child("Event").child(this.eventid).child("Task").child(this.key)
                .child("status").setValue("C")
        }
        if (action == "active") {
            myRef.child("User").child("Event").child(this.eventid).child("Task").child(this.key)
                .child("status").setValue("A")
        }
    }

    fun deleteTask() {
        myRef.child("User").child("Event").child(this.eventid).child("Task").child(this.key).removeValue()
    }

    fun addTask() {
        val postRef=myRef.child("User").child("Event").child(this.eventid).child("Task").push()

        val tasks = hashMapOf(
            "name" to name,
            "budget" to budget,
            "date" to date,
            "category" to category,
            "status" to "A",
            "eventid" to eventid
        )

        postRef.setValue(tasks as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
            }
    }
}
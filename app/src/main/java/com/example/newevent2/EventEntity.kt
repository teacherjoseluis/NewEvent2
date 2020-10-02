package com.example.newevent2

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.*

class EventEntity : Event() {

    var taskcount = 0
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun countEventTasks(action: String) { // action = "A" & "C"
        val postRef = myRef.child("User").child("Event").child(this.key).child("Task")
        val countListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (snapshot in p0.children) {
                        val taskitem = snapshot.getValue(Task::class.java)

                        if (taskitem!!.status == action) {
                            taskcount += 1
                        }
                        if (action == "") {
                            taskcount += 1
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(countListener)
    }

    fun gettaskcount(taskcount: Int) {
        this.taskcount = taskcount
    }
}
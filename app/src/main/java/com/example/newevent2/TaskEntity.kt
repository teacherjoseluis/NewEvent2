package com.example.newevent2

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskEntity : Task() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun getTasksEvent(dataFetched: FirebaseSuccessListenerTask) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")

        var taskcountpending = 0
        var taskcountcompleted = 0
        var sumbudget = 0.0F

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val re = Regex("[^A-Za-z0-9 ]")
                taskcountpending = 0
                taskcountcompleted = 0
                sumbudget = 0.0F

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!

                    sumbudget += re.replace(taskitem.budget, "").dropLast(2).toFloat()
                    when (taskitem.status) {
                        "A" -> taskcountpending += 1
                        "C" -> taskcountcompleted += 1
                    }
                }
                dataFetched.onTasksEvent(taskcountpending, taskcountcompleted, sumbudget)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun getTasksList(dataFetched: FirebaseSuccessListenerTask) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        var tasklist = ArrayList<Task>()

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist.clear()

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)

                    if (taskitem!!.category == category && taskitem!!.status == status) {
                        taskitem!!.key = snapshot.key.toString()
                        tasklist.add(taskitem!!)
                    }
                }
                dataFetched.onTasksList(tasklist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun getTaskStats(dataFetched: FirebaseSuccessListenerTask) {
        var sumbudget: Float
        var countactive: Int
        var countcompleted: Int

        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                countactive = 0
                countcompleted = 0
                sumbudget = 0.0f

                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)

                    if (taskitem!!.category == category) {
                        val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                        sumbudget += budgetamount.toFloat()
                        if (taskitem.status == "A") {
                            countactive += 1
                        } else if (taskitem.status == "C") {
                            countcompleted += 1
                        }
                    }
                }
                dataFetched.onTasksEvent(countactive, countcompleted, sumbudget)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun editTask() {
        val postRef=myRef.child("User").child("Event").child(this.eventid).child("Task").child(this.key)

        val tasks = hashMapOf(
            "name" to name,
            "budget" to budget,
            "date" to date,
            "category" to category,
            "eventid" to eventid,
            "status" to status
        )

        postRef.setValue(tasks as Map<String, Any>)
            .addOnFailureListener {
                return@addOnFailureListener
            }
            .addOnSuccessListener {
                return@addOnSuccessListener
            }
    }

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
        myRef.child("User").child("Event").child(this.eventid).child("Task").child(this.key)
            .removeValue()
    }

    fun addTask() {
        val postRef =
            myRef.child("User").child("Event").child(this.eventid).child("Task").push()

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
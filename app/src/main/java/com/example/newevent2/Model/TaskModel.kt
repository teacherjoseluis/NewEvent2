package com.example.newevent2.Model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerTask
import com.example.newevent2.getUserSession
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class TaskModel {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    fun getTaskStats(
        userid: String,
        eventid: String,
        category: String,
        dataFetched: FirebaseSuccessStatsTask
    ) {
        var sumbudget: Float
        var countactive: Int
        var countcompleted: Int

        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task")

        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                countactive = 0 // Active/Open Tasks
                countcompleted = 0 // Tasks Completed
                sumbudget = 0.0f // Budget Amount for all Tasks

                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!
                    if (category != "") {
                        if (taskitem.category == category) {
                            val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                            sumbudget += budgetamount.toFloat()
                            if (taskitem.status == "A") {
                                countactive += 1
                            } else if (taskitem.status == "C") {
                                countcompleted += 1
                            }
                        }
                    } else {
                        val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                        sumbudget += budgetamount.toFloat()
                        if (taskitem!!.status == "A") {
                            countactive += 1
                        } else if (taskitem.status == "C") {
                            countcompleted += 1
                        }
                    }
                }
                dataFetched.onTasksStats(countactive, countcompleted, sumbudget)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun getTasksBudget(userid: String, eventid: String, dataFetched: FirebaseSuccessTaskBudget) {
        var sumbudget: Float
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task")
        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                sumbudget = 0.0f // Budget Amount for all Tasks

                val re = Regex("[^A-Za-z0-9 ]")
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!
                    val budgetamount = re.replace(taskitem.budget, "").dropLast(2)
                    sumbudget += budgetamount.toFloat()
                }
                dataFetched.onTasksBudget(sumbudget)
            }

            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun getDueNextTask(
        userid: String,
        eventid: String,
        dataFetched: FirebaseSuccessTask
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task")
                .orderByChild("date")
        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var duenexttask = Task()
                val todaydate = Date()
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!
                    if (taskitem.status == "A") {
                        if (todaydate.before(SimpleDateFormat("dd/MM/yyyy").parse(taskitem.date))) {
                            duenexttask = taskitem
                            break
                        }
                    }
                }
                dataFetched.onTask(duenexttask)
            }

            override fun onCancelled(p0: DatabaseError) {
                println("loadPost:onCancelled ${p0.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    interface FirebaseSuccessStatsTask {
        fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float)
    }

    interface FirebaseSuccessTaskBudget {
        fun onTasksBudget(sumbudget: Float)
    }

    interface FirebaseSuccessTask {
        fun onTask(task: Task)
    }
}

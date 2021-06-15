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
import java.lang.Exception
import java.sql.Time
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
                            taskitem!!.key = snapshot.key.toString()
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

    fun getTaskdetail(
        userid: String,
        eventid: String,
        taskid: String,
        dataFetched: FirebaseSuccessTask
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").child(taskid)

        val taskListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val taskitem = p0.getValue(Task::class.java)!!
                taskitem!!.key = p0.key.toString()
                dataFetched.onTask(taskitem)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun getTasksList(
        userid: String,
        eventid: String,
        category: String,
        status: String,
        dataFetched: FirebaseSuccessTaskList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").orderByChild("date")
        var tasklist = ArrayList<Task>()

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist.clear()

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)

                    if (taskitem!!.status == status) {
                        if (category != "") {
                            if (taskitem!!.category == category) {
                                taskitem!!.key = snapshot.key.toString()
                                tasklist.add(taskitem!!)
                            }
                        } else {
                            taskitem!!.key = snapshot.key.toString()
                            tasklist.add(taskitem!!)
                        }
                    }
                }
                dataFetched.onTaskList(tasklist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun getAllTasksList(
        userid: String,
        eventid: String,
        //category: String,
        //status: String,
        dataFetched: FirebaseSuccessTaskList
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").orderByChild("date")
        var tasklist = ArrayList<Task>()

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist.clear()

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)
                    taskitem!!.key = snapshot.key.toString()
                    tasklist.add(taskitem!!)
                }
                dataFetched.onTaskList(tasklist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun addTask(
        userid: String,
        eventid: String,
        task: Task,
        tasksactive: Int,
        taskaddedflag: FirebaseAddEditTaskSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created task
        val timestamp = Time(System.currentTimeMillis())
        val taskdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------

        val taskadd = hashMapOf(
            "name" to task.name,
            "budget" to task.budget,
            "date" to task.date,
            "category" to task.category,
            "status" to ACTIVESTATUS,
            "createdatetime" to sdf.format(taskdatetime)
        )

        postRef.setValue(taskadd as Map<String, Any>)
            .addOnFailureListener {
                taskaddedflag.onTaskAddedEdited(false)
            }
            .addOnSuccessListener {
                    val usermodel = UserModel(userid)
                    usermodel.editUserTaskflag(ACTIVEFLAG)
                    usermodel.editUserAddTask(tasksactive + 1)
                    taskaddedflag.onTaskAddedEdited(true)
            }
    }

    fun editTask(
        userid: String,
        eventid: String,
        task: Task,
        taskeditedflag: FirebaseAddEditTaskSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").child(task.key)

        val taskedit = hashMapOf(
            "name" to task.name,
            "budget" to task.budget,
            "date" to task.date,
            "category" to task.category,
            "status" to task.status,
            "createdatetime" to task.createdatetime
        )

        postRef.updateChildren(taskedit as Map<String, Any>)
            .addOnSuccessListener {
                taskeditedflag.onTaskAddedEdited(true)
            }
    }

    fun deleteTask(userid: String, eventid: String, task: Task) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").child(task.key)
                .removeValue()
        //todo ("Need to implement a callback that will check if the user has no longer tasks associated. In such a case the associated flag in the User Profile will be set as "N"")
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

    interface FirebaseSuccessTaskList {
        fun onTaskList(list: ArrayList<Task>)
    }

    interface FirebaseAddEditTaskSuccess {
        fun onTaskAddedEdited(flag: Boolean)
    }

    companion object {
        const val ACTIVESTATUS = "A"
        const val ACTIVEFLAG = "Y"
    }
}

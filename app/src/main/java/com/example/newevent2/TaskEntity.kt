package com.example.newevent2

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TaskEntity : Task() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    //At some point when the refactor on all of the TaskEntity applications ends, I'll be making the call to the
    //usersession function here and avoid repeating the calling code in each of the methods of this class

    fun getTasksEvent(context: Context, dataFetched: FirebaseSuccessListenerTask) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task")
                .orderByChild("date")

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasksDatesEvent(month: Int, dataFetched: FirebaseSuccessListenerTaskCalendar) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val taskdates = ArrayList<Date>()

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val str = taskitem.date.split("/")
                        if (month == Integer.parseInt(str[1]) - 1) {
                            taskdates.add(SimpleDateFormat("dd/MM/yyyy").parse(taskitem.date))
                        }
                    }
                }
                dataFetched.onTasksDatesEvent(taskdates)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun getTasksperDay(calendar: Calendar, dataFetched: FirebaseSuccessListenerTaskCalendar) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        var tasklist = ArrayList<Task>()

        val tasklListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)!!
                    if (SimpleDateFormat("dd/MM/yyyy").parse(taskitem.date) == calendar.time) {
                        tasklist.add(taskitem)
                    }
                }
                dataFetched.onTasksperDay(tasklist)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    fun getTasksList(context: Context, dataFetched: FirebaseSuccessListenerTask) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task")
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

    fun getTaskStats(context: Context, dataFetched: FirebaseSuccessListenerTask) {
        var sumbudget: Float
        var countactive: Int
        var countcompleted: Int


        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task")

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

    fun getDueNextTask(context: Context, dataFetched: FirebaseSuccessListenerTaskWelcome) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task")
                .orderByChild("date")
        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //val todaydate= SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
                var duenexttask = Task()
                val todaydate = Date()
                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)
                    if (taskitem!!.status == "A") {
                        if (todaydate.before(SimpleDateFormat("dd/MM/yyyy").parse(taskitem.date))) {
                            duenexttask = taskitem
                        }
                    }
                }
                dataFetched.onTask(duenexttask)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun getRecentCreatedTask(context: Context, dataFetched: FirebaseSuccessListenerTaskWelcome) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task")
                .orderByChild("createdatetime")
        val taskListenerActive = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //val todaydate= SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
                var taskcreated = Task()
                loop@ for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)
                    if (taskitem!!.status == "A" && taskitem.createdatetime.isNotEmpty()) {
                        taskcreated = taskitem
                        break@loop
                    }
                }
                dataFetched.onTask(taskcreated)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        postRef.addValueEventListener(taskListenerActive)
    }

    fun editTask(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task").child(this.key)

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
                //saveLog(context, "UPDATE", "task", key, name)
                return@addOnSuccessListener
            }
    }

    fun editTask(context: Context, action: String) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task").child(this.key)

        if (action == "complete") {
            postRef.child("status").setValue("C")
                .addOnSuccessListener {
                    //saveLog(context, "UPDATE", "task", key, name)
                }
        }
        if (action == "active") {
            postRef.child("status").setValue("A")
                .addOnSuccessListener {
                    //saveLog(context, "UPDATE", "task", key, name)
                }
        }
    }

    fun deleteTask(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task").child(this.key)
            .removeValue()
    }

    fun addTask(context: Context) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
                .child("Task").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created task
        val timestamp = Time(System.currentTimeMillis())
        val taskdatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------


        val tasks = hashMapOf(
            "name" to name,
            "budget" to budget,
            "date" to date,
            "category" to category,
            "status" to "A",
            //"eventid" to eventid,
            "createdatetime" to sdf.format(taskdatetime)
        )

        postRef.setValue(tasks as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                val taskkey = postRef.key.toString()
                //saveLog(context, "INSERT", "task", taskkey, name)
            }
    }
}
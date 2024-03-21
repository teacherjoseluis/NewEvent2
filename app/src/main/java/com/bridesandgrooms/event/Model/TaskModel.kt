package com.bridesandgrooms.event.Model

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.CoRAddEditTask
import com.bridesandgrooms.event.Functions.CoRDeleteTask
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TaskModel : CoRAddEditTask, CoRDeleteTask{

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var nexthandler: CoRAddEditTask? = null
    var nexthandlerdel: CoRDeleteTask? = null

    @ExperimentalCoroutinesApi
    suspend fun getTasks(userid: String, eventid: String): ArrayList<Task> {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").orderByChild("date")
        val taskList = ArrayList<Task>()

        try {
            for (snapChild in postRef.awaitsSingle()?.children!!) {
                val taskItem = snapChild.getValue(Task::class.java)
                taskItem!!.key = snapChild.key.toString()
                taskList.add(taskItem)
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return taskList
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

        val tasklist = ArrayList<Task>()

        val tasklListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                tasklist.clear()

                for (snapshot in p0.children) {
                    val taskitem = snapshot.getValue(Task::class.java)
                    taskitem!!.key = snapshot.key.toString()
                    tasklist.add(taskitem)
                }
                Log.d(TAG, "Number of tasks retrieved ${tasklist.count()}")
                dataFetched.onTaskList(tasklist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(tasklListenerActive)
    }

    private fun addTask(
        userid: String,
        eventid: String,
        task: Task,
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
        task.status = ACTIVESTATUS
        task.createdatetime = sdf.format(taskdatetime)
        //---------------------------------------
        val taskadd = hashMapOf(
            "name" to task.name,
            "budget" to task.budget,
            "date" to task.date,
            "category" to task.category,
            "eventid" to task.eventid,
            "status" to ACTIVESTATUS,
            "createdatetime" to sdf.format(taskdatetime)
        )

        postRef.setValue(taskadd as Map<String, Any>)
            .addOnSuccessListener {
                task.key = postRef.key.toString()
                taskaddedflag.onTaskAddedEdited(true, task)
                Log.d(TAG, "Task ${task.name} successfully added on ${sdf.format(taskdatetime)}")
            }
            .addOnFailureListener {
                taskaddedflag.onTaskAddedEdited(false, task)
                Log.e(TAG, "Task ${task.name} failed to be added")
            }
    }

    private fun editTask(
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
            "eventid" to task.eventid,
            "status" to task.status,
            "createdatetime" to task.createdatetime
        )

        postRef.updateChildren(taskedit as Map<String, Any>)
            .addOnSuccessListener {
                taskeditedflag.onTaskAddedEdited(true, task)
                Log.d(TAG, "Task ${task.name} successfully edited")
            }
            .addOnFailureListener {
                taskeditedflag.onTaskAddedEdited(false, task)
                Log.e(TAG, "Task ${task.name} failed to be edited")
            }
    }

    private fun deleteTask(
        userid: String,
        eventid: String,
        task: Task,
        taskdeletedflag: FirebaseDeleteTaskSuccess
    ) {
        val postRef =
            myRef.child("User").child(userid).child("Event").child(eventid)
                .child("Task").child(task.key)
                .removeValue()
                .addOnSuccessListener {
                    taskdeletedflag.onTaskDeleted(true)
                    Log.d(TAG, "Task ${task.name} successfully deleted")
                }
                .addOnFailureListener {
                    taskdeletedflag.onTaskDeleted(false)
                    Log.e(TAG, "Task ${task.name} failed to be deleted")
                }
    }

    @ExperimentalCoroutinesApi
    suspend fun Query.awaitsSingle(): DataSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        continuation.resume(snapshot)
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = when (error.toException()) {
                        is FirebaseException -> error.toException()
                        else -> Exception("The Firebase call for reference $this was cancelled")
                    }
                    continuation.resumeWithException(exception)
                }
            }
            continuation.invokeOnCancellation { this.removeEventListener(listener) }
            this.addListenerForSingleValueEvent(listener)
        }

    override fun onAddEditTask(context: Context, user: User, task: Task) {
//        val userdbhelper = UserDBHelper(context)
//        val user = userdbhelper.getUser(userdbhelper.getUserKey())!!
        if (task.key.isEmpty()) {
            addTask(
                //userid,
                user.userid!!,
                user.eventid,
                task,
                object : FirebaseAddEditTaskSuccess {
                    override fun onTaskAddedEdited(flag: Boolean, task: Task) {
                        if (flag) {
                            nexthandler?.onAddEditTask(context, user, task)
                        }
                    }
                })
        } else {
            editTask(
                user.userid!!, user.eventid, task, object : FirebaseAddEditTaskSuccess {
                    override fun onTaskAddedEdited(flag: Boolean, task: Task) {
                        if (flag) {
                            nexthandler?.onAddEditTask(context, user, task)
                        }
                    }
                }
            )
        }
    }

    override fun onDeleteTask(context: Context, user:User, task: Task) {
//        val userdbhelper = UserDBHelper(context)
//        val user = userdbhelper.getUser(userdbhelper.getUserKey())!!
        deleteTask(
            user.userid!!,
            user.eventid,
            task,
            object : FirebaseDeleteTaskSuccess {
                override fun onTaskDeleted(flag: Boolean) {
                    if (flag) {
                        nexthandlerdel?.onDeleteTask(context, user, task)
                    }
                }
            })
    }

//    interface FirebaseSuccessStatsTask {
//        fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float)
//    }

//    interface FirebaseSuccessTaskBudget {
//        fun onTasksBudget(sumbudget: Float)
//    }

    interface FirebaseSuccessTaskList {
        fun onTaskList(list: ArrayList<Task>)
    }

    interface FirebaseAddEditTaskSuccess {
        fun onTaskAddedEdited(flag: Boolean, task: Task)
    }

    interface FirebaseDeleteTaskSuccess {
        fun onTaskDeleted(flag: Boolean)
    }

    companion object {
        const val ACTIVESTATUS = "A"
        const val COMPLETESTATUS = "C"
        const val ACTIVEFLAG = "Y"
        const val INACTIVEFLAG = "N"
        const val TAG = "TaskModel"
    }
}

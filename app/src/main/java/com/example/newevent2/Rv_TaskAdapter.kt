package com.example.newevent2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Category.Companion.getCategory
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.deleteTask
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import kotlinx.android.synthetic.main.task_item_layout.view.*
import java.util.*

class Rv_TaskAdapter(val taskList: MutableList<Task>) :
    RecyclerView.Adapter<Rv_TaskAdapter.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context
    var taskmodel = TaskModel()
    lateinit var taskdbhelper: TaskDBHelper
    lateinit var usermodel: UserModel

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.taskname?.text = taskList[p1].name
        p0.taskcategory?.text = taskList[p1].category
        p0.taskdate?.text = taskList[p1].date
        p0.taskbudget?.text = taskList[p1].budget
        val resourceId = context.resources.getIdentifier(getCategory(taskList[p1].category).drawable, "drawable",
            context.packageName)
        p0.categoryavatar?.setImageResource(resourceId)

        p0.itemView.setOnClickListener {
            val taskdetail = Intent(context, TaskCreateEdit::class.java)
            taskdetail.putExtra("task", taskList[p1])
            context.startActivity(taskdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
        val taskcategory: TextView? = itemView.findViewById<TextView>(R.id.taskcategory)
        val taskdate: TextView? = itemView.findViewById<TextView>(R.id.taskdate)
        val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudgets)
        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        if (action == CHECKACTION) {
            val taskswift = taskList[position]
            taskList.removeAt(position)
            notifyItemRemoved(position)
            taskswift.status = COMPLETETASK
            editTask(context, taskswift)

            val snackbar = Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    editTask(context, taskswift)
//                }
            snackbar.show()
        }
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        //val user = com.example.newevent2.Functions.getUserSession(context!!)
        val taskswift = taskList[position]
        val taskbackup = Task().apply {
            name = taskswift.name
            budget = taskswift.budget
            date = taskswift.date
            category = taskswift.category
            status = taskswift.status
            createdatetime = taskswift.createdatetime
        }

        //delNotification(taskswift)

        if (action == DELETEACTION) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
            deleteTask(context, taskswift)

            val snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    addTask(context, taskbackup)
//                    //addNotification(taskbackup)
//                }
            snackbar.show()
        } else if (action == UNDOACTION) {
            taskList.add(taskswift)
            notifyItemInserted(taskList.lastIndex)
            taskswift.status = ACTIVETASK
            editTask(context, taskswift)
            //addNotification(taskbackup)
        }
    }

    //-------------------------------------------------------------------------------
    // Creating Notification for Tasks
    //-------------------------------------------------------------------------------
//    private fun addNotification(task: Task) {
//        // Job ID must be unique if you have multiple jobs scheduled
//        var jobID = NotificationID.getID()
//
//        var gson = Gson()
//        var json = gson.toJson(task)
//        var bundle = PersistableBundle()
//        bundle.putString("task", json)
//
//        // Get fake user set time (a future time 1 min from current time)
//        val (userSetHourOfDay, userSetMinute) = getMockUserSetTime()
//        val timeToWaitBeforeExecuteJob = calculateTimeDifferenceMs(userSetHourOfDay, userSetMinute)
//        (context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).run {
//            schedule(
//                JobInfo.Builder(
//                    jobID,
//                    ComponentName(context, NotificationJobService::class.java)
//                )
//                    // job execution will be delayed by this amount of time
//                    .setMinimumLatency(timeToWaitBeforeExecuteJob)
//                    // job will be run by this deadline
//                    .setOverrideDeadline(timeToWaitBeforeExecuteJob)
//                    .setExtras(bundle)
//                    .build()
//            )
//        }
//    }

    //-------------------------------------------------------------------------------
//    private fun delNotification(task: Task) {
//        val notificationManager =
//            context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(task.key, 0)
//    }
    //-------------------------------------------------------------------------------

    // Returns a pair ( hourOfDay, minute ) that represents a future time,
    // 1 minute after the current time
    private fun getMockUserSetTime(): Pair<Int, Int> {
        val calendar = Calendar.getInstance().apply {
            // add just 1 min from current time
            add(Calendar.MINUTE, 1)
        }
        return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    // Calculate time difference relative to current time in ms
    private fun calculateTimeDifferenceMs(hourOfDay: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val then = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        return then.timeInMillis - now.timeInMillis
    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val CHECKACTION = "check"
        const val DELETEACTION = "delete"
        const val UNDOACTION = "undo"
        const val TAG = "Rv_TaskAdapter"
    }
}




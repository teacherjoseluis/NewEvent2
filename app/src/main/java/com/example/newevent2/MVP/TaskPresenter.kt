package com.example.newevent2.MVP

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.FirebaseSuccessListenerLogWelcome
import com.example.newevent2.Functions.FirebaseGetLogSuccess
import com.example.newevent2.LoginView
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.Functions.getLog
import com.example.newevent2.Functions.removeLog
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.WelcomeView
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TaskPresenter(
    view: WelcomeView,
    val userid: String,
    val eventid: String
) {
    var viewWelcome: WelcomeView = view

    fun getTaskStats(category: String) {
        val task = TaskModel()
        task.getTaskStats(userid, eventid, category, object : TaskModel.FirebaseSuccessStatsTask {
            override fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                if (taskpending == 0 && taskcompleted == 0) {
                    //There are no tasks created
                    viewWelcome.onViewTaskError("BLANK_STATS")
                } else {
                    //Show the stats
                    viewWelcome.onViewTaskStatsSuccess(taskpending, taskcompleted, sumbudget)
                }
            }
        })
    }

    fun getDueNextTask() {
        val task = TaskModel()
        task.getDueNextTask(userid, eventid, object : TaskModel.FirebaseSuccessTask {
            override fun onTask(task: Task) {
                if (task.key == "") {
                    viewWelcome.onViewTaskError("BLANK_TASK")
                } else {
                    viewWelcome.onViewNextTaskSuccess(task)
                }
            }
        })
    }

    interface ViewTaskWelcomeActivity {
        fun onViewTaskStatsSuccess(taskpending: Int, taskcompleted: Int, sumbudget: Float)
        fun onViewNextTaskSuccess(task: Task)
        fun onViewTaskError(errcode: String)
    }
}
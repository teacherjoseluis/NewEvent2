package com.example.newevent2.MVP

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView
import com.example.newevent2.MainEventSummary
import com.example.newevent2.TaskPayment_Tasks
import java.util.ArrayList

class TaskPresenter {

    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var viewDashboard: DashboardView
    lateinit var fragmentEventSummary: MainEventSummary
    lateinit var fragmentTaskPaymentTask: TaskPayment_Tasks

    constructor(view: DashboardView, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        viewDashboard = view
    }

    constructor(fragment: MainEventSummary, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        fragmentEventSummary = fragment
        inflatedView = view
    }

    constructor(fragment: TaskPayment_Tasks, view: View, userid: String, eventid: String) {
        this.userid = userid
        this.eventid = eventid
        fragmentTaskPaymentTask = fragment
        inflatedView = view
    }

    fun getTaskStats(category: String = "") {
        val task = TaskModel()
        task.getTaskStats(userid, eventid, category, object : TaskModel.FirebaseSuccessStatsTask {
            override fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                if (taskpending == 0 && taskcompleted == 0) {
                    //There are no tasks created
                    when {
                        ::viewDashboard.isInitialized -> {
                            viewDashboard.onViewTaskError("BLANK_STATS")
                        }
                        ::fragmentEventSummary.isInitialized -> {
                            fragmentEventSummary.onViewTaskErrorFragment(inflatedView, "BLANK_STATS")
                        }
                        ::fragmentTaskPaymentTask.isInitialized -> {
                            fragmentTaskPaymentTask.onViewTaskErrorFragment(inflatedView, "BLANK_STATS")
                        }
                    }
                } else {
                    //Show the stats
                    when {
                        ::viewDashboard.isInitialized -> {
                            viewDashboard.onViewTaskStatsSuccess(taskpending, taskcompleted, sumbudget)
                        }
                        ::fragmentEventSummary.isInitialized -> {
                            fragmentEventSummary.onViewTaskStatsSuccessFragment(
                                inflatedView,
                                taskpending,
                                taskcompleted,
                                sumbudget
                            )
                        }
                        ::fragmentTaskPaymentTask.isInitialized -> {
                            fragmentTaskPaymentTask.onViewTaskStatsSuccessFragment(
                                inflatedView,
                                taskpending,
                                taskcompleted,
                                sumbudget
                            )
                        }
                    }
                }
            }
        })
    }

    fun getTasksList(category: String, status: String) {
        val task = TaskModel()
        task.getTasksList(
            userid,
            eventid,
            category,
            status,
            object : TaskModel.FirebaseSuccessTaskList {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onTaskList(list: ArrayList<Task>) {
                    if (list.isNotEmpty()) {
                        fragmentTaskPaymentTask.onViewTaskListFragment(
                            inflatedView,
                            category,
                            status,
                            list
                        )
                    } else {
                        fragmentTaskPaymentTask.onViewTaskListErrorFragment(
                            inflatedView,
                            category,
                            status,
                            "NO_TASKS"
                        )
                    }
                }
            })
    }

    fun getDueNextTask() {
        val task = TaskModel()
        task.getDueNextTask(userid, eventid, object : TaskModel.FirebaseSuccessTask {
            override fun onTask(task: Task) {
                if (task.key == "") {
                    viewDashboard.onViewTaskError("BLANK_TASK")
                } else {
                    viewDashboard.onViewNextTaskSuccess(task)
                }
            }
        })
    }

    interface ViewTaskWelcomeActivity {
        fun onViewTaskStatsSuccess(taskpending: Int, taskcompleted: Int, sumbudget: Float)
        fun onViewNextTaskSuccess(task: Task)
        fun onViewTaskError(errcode: String)
    }

    interface ViewTaskFragment {
        fun onViewTaskStatsSuccessFragment(
            inflatedView: View,
            taskpending: Int,
            taskcompleted: Int,
            sumbudget: Float
        )

        fun onViewTaskErrorFragment(inflatedView: View, errcode: String)
    }

    interface ViewTaskList {
        fun onViewTaskListFragment(
            inflatedView: View,
            category: String,
            status: String,
            list: ArrayList<Task>
        )

        fun onViewTaskListErrorFragment(
            inflatedView: View,
            category: String,
            status: String,
            errcode: String
        )
    }
}
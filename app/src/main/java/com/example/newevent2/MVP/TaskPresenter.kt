package com.example.newevent2.MVP

import android.view.View
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView
import com.example.newevent2.MainEventSummary

class TaskPresenter {

    var userid = ""
    var eventid = ""
    lateinit var inflatedView: View
    lateinit var viewDashboard: DashboardView
    lateinit var fragmentEventSummary: MainEventSummary

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

    fun getTaskStats(category: String = "") {
        val task = TaskModel()
        task.getTaskStats(userid, eventid, category, object : TaskModel.FirebaseSuccessStatsTask {
            override fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                if (taskpending == 0 && taskcompleted == 0) {
                    //There are no tasks created
                    if (::viewDashboard.isInitialized) {
                        viewDashboard.onViewTaskError("BLANK_STATS")
                    } else if (::fragmentEventSummary.isInitialized) {
                        fragmentEventSummary.onViewTaskErrorFragment(inflatedView,"BLANK_STATS")
                    }
                } else {
                    //Show the stats
                    if (::viewDashboard.isInitialized) {
                        viewDashboard.onViewTaskStatsSuccess(taskpending, taskcompleted, sumbudget)
                    } else if (::fragmentEventSummary.isInitialized) {
                        fragmentEventSummary.onViewTaskStatsSuccessFragment(
                            inflatedView,
                            taskpending,
                            taskcompleted,
                            sumbudget
                        )
                    }
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
}
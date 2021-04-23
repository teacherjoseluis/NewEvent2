package com.example.newevent2.MVP

import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.DashboardView

class TaskPresenter(
    view: DashboardView,
    val userid: String,
    val eventid: String
) {
    var viewDashboard: DashboardView = view

    fun getTaskStats(category: String = "") {
        val task = TaskModel()
        task.getTaskStats(userid, eventid, category, object : TaskModel.FirebaseSuccessStatsTask {
            override fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                if (taskpending == 0 && taskcompleted == 0) {
                    //There are no tasks created
                    viewDashboard.onViewTaskError("BLANK_STATS")
                } else {
                    //Show the stats
                    viewDashboard.onViewTaskStatsSuccess(taskpending, taskcompleted, sumbudget)
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
}
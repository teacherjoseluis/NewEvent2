package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.TaskPaymentTasks
import kotlin.collections.ArrayList

class TaskPaymentTasksPresenter(
    val context: Context,
    val fragment: TaskPaymentTasks,
    val view: View,
    private val taskcategory: String,
    private val status: String
) :
    TaskPresenter.TaskList {

    private var presentertask: TaskPresenter = TaskPresenter(context!!, this)

    init {
        presentertask.getTasksList()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        var filteredtasklistactive = ArrayList<Task>()
        for (task in list) {
            if ((task.category == taskcategory) || (taskcategory == "")) {
                if (task.status == status) {
                    filteredtasklistactive.add(task)
                }
            }
        }
        fragment.onTPTasks(view, filteredtasklistactive)
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTPTasksError(view, TaskPresenter.ERRCODETASKS)
    }

    interface TPTasks {
        fun onTPTasks(
            inflatedView: View,
            list: ArrayList<Task>
        )

        fun onTPTasksError(inflatedView: View, errcode: String)
    }
}
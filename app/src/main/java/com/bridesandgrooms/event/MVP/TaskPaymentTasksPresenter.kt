package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.TaskPaymentTasks

class TaskPaymentTasksPresenter(
    val context: Context,
    val fragment: TaskPaymentTasks,
    val view: View,
    private val taskcategory: String,
    private val status: String
) :
    TaskPresenter.TaskList {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)

    init {
        presentertask.getTasksList()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        val filteredtasklistactive = ArrayList<Task>()
        for (task in list) {
            if ((task.category == taskcategory) || (taskcategory.isEmpty())) {
                if (task.status == status) {
                    filteredtasklistactive.add(task)
                }
            }
        }
        fragment.onTPTasks(filteredtasklistactive)
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTPTasksError(TaskPresenter.ERRCODETASKS)
    }

    interface TPTasks {
        fun onTPTasks(
            list: ArrayList<Task>
        )

        fun onTPTasksError(errcode: String)
    }
}
package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.TaskPaymentTasks
import com.bridesandgrooms.event.UI.ExportPDF

class ExportPDFPresenter(
    val context: Context,
    val fragment: ExportPDF,
    //val view: View,
    private val taskcategory: String
    //private val status: String
) :
    TaskPresenter.TaskList {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)

    init {
        presentertask.getTasksList()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        val filteredtasklistactive = ArrayList<Task>()
        for (task in list) {
            if ((task.category == taskcategory) || (taskcategory == "")) {
                //if (task.status == status) {
                filteredtasklistactive.add(task)
                //}
            }
        }
        fragment.onEPDFTasks(filteredtasklistactive)
    }

    override fun onTaskListError(errcode: String) {
        fragment.onEPDFTasksError(TaskPresenter.ERRCODETASKS)
    }

    interface EPDFTasks {
        fun onEPDFTasks(
            list: ArrayList<Task>
        )

        fun onEPDFTasksError(errcode: String)
    }
}
package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.UI.Activities.ExportPDF

class ExportPDFPresenter(
    val context: Context,
    val fragment: ExportPDF,
    //val view: View,
    private val taskcategory: String
    //private val status: String
) :
    TaskPresenter.TaskList {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)
    private val mHandler = Handler(Looper.getMainLooper())

    fun getTasksList() {
        Thread {
            presentertask.getTasksList()
        }.start()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        mHandler.post {
            val filteredtasklistactive = ArrayList<Task>()
            for (task in list) {
                if ((task.category == taskcategory) || (taskcategory.isEmpty())) {
                    //if (task.status == status) {
                    filteredtasklistactive.add(task)
                    //}
                }
            }
            fragment.onEPDFTasks(filteredtasklistactive)
        }
    }

    override fun onTaskListError(errcode: String) {
        mHandler.post {
            fragment.onEPDFTasksError(TaskPresenter.ERRCODETASKS)
        }
    }

    interface EPDFTasks {
        fun onEPDFTasks(
            list: ArrayList<Task>
        )

        fun onEPDFTasksError(errcode: String)
    }
}
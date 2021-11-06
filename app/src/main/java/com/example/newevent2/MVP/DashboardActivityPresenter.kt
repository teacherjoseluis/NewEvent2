package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.DashboardActivity
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal

class DashboardActivityPresenter(
    val context: Context,
    val fragment: DashboardActivity,
    val view: View
) :
    TaskPresenter.TaskList {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)

    init {
        presentertask.getTasksList()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        //Converting a Task list into a TaskJournal list
        // Unique Dates Array
        val taskdatelist: ArrayList<String> = ArrayList()
        for (task in list) {
            if (!taskdatelist.contains(task.date)) {
                taskdatelist.add(task.date)
            }
        }
        // Araylist of Tasks when the Date is the Key
        val taskjournal: ArrayList<TaskJournal> = ArrayList()
        val taskjournallist: ArrayList<Task> = ArrayList()
        for (taskdates in taskdatelist) {
            taskjournallist.clear()
            for (task in list) {
                if (task.date == taskdates) {
                    taskjournallist.add(task)
                }
            }
            val newtasklist: ArrayList<Task> = ArrayList(taskjournallist.size)
            for (tasklist in taskjournallist) newtasklist.add(tasklist)
            taskjournal.add(TaskJournal(converttoDate(taskdates), newtasklist))
        }
        // This is supposed to sort TaskJournal based on the date
        taskjournal.sortWith(Comparator { o1, o2 ->
            if (o1.date == null || o2.date == null) 0 else o1.date
                .compareTo(o2.date)
        })
        fragment.onTaskJournal(view, taskjournal)
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTaskJournalError(view, ERRCODETASKS)
    }

    interface TaskJournalInterface {
        fun onTaskJournal(
            inflatedView: View,
            list: ArrayList<TaskJournal>
        )

        fun onTaskJournalError(inflatedView: View, errcode: String)
    }
}


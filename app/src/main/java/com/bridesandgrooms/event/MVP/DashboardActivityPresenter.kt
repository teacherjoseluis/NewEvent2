package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.DashboardActivity
import com.bridesandgrooms.event.Functions.converttoDate
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskJournal

class DashboardActivityPresenter(
    val context: Context,
    val fragment: DashboardActivity
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
        fragment.onTaskJournal(taskjournal)
    }

    override fun onTaskListError(errcode: String) {
        fragment.onTaskJournalError(ERRCODETASKS)
    }

    interface TaskJournalInterface {
        fun onTaskJournal(
            list: ArrayList<TaskJournal>
        )

        fun onTaskJournalError(errcode: String)
    }
}


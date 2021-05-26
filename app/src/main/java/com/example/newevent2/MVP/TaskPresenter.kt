package com.example.newevent2.MVP

import Application.Cache
import Application.CacheCategory
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.newevent2.DashboardActivity
import com.example.newevent2.DashboardEvent
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.MainEventSummary
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.TaskPayment_Tasks
import java.util.*
import kotlin.collections.ArrayList

class TaskPresenter : ViewModel, Cache.ArrayListCacheData, Cache.SingleItemCacheData {

    private var activefragment = ""
    private var inflatedView: View

    private lateinit var mContext: Context
    private lateinit var cachetaskjournal: Cache<TaskJournal>
    private lateinit var cachetask: Cache<Task>

    private lateinit var fragmentDE: DashboardEvent
    private lateinit var fragmentDA: DashboardActivity
    private lateinit var fragmentTT: TaskPayment_Tasks
    private lateinit var fragmentME: MainEventSummary

    var userid = ""
    var eventid = ""

    private var taskcategory = ""
    private var taskstatus = ""

    constructor(context: Context, fragment: DashboardEvent, view: View) {
        fragmentDE = fragment
        inflatedView = view
        mContext = context
        activefragment = "DE"
    }

    constructor(context: Context, fragment: DashboardActivity, view: View) {
        fragmentDA = fragment
        inflatedView = view
        mContext = context
        activefragment = "DA"
    }

    constructor(fragment: TaskPayment_Tasks, view: View) {
        fragmentTT = fragment
        inflatedView = view
        activefragment = "TT"
    }

    constructor(fragment: MainEventSummary, view: View) {
        fragmentME = fragment
        inflatedView = view
        activefragment = "ME"
    }

    fun getTaskStats(category: String = "") {
        // It may need to be added to the cache layer, for the moment is directly being consumed from Firebase
        // Let's consider it to be included in the cache in a later phase
        val task = TaskModel()
        task.getTaskStats(userid, eventid, category, object : TaskModel.FirebaseSuccessStatsTask {
            override fun onTasksStats(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                if (taskpending == 0 && taskcompleted == 0) {
                    //There are no tasks created
                    when (activefragment) {
                        "DE" -> fragmentDE.onTaskStatsError(inflatedView, "BLANK_STATS")
                        "TT" -> fragmentTT.onTaskStatsError(inflatedView, "BLANK_STATS")
                    }
                } else {
                    when (activefragment) {
                        "DE" -> fragmentDE.onTasksStats(
                            inflatedView,
                            taskpending,
                            taskcompleted,
                            sumbudget
                        )
                        "TT" -> fragmentTT.onTasksStats(
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getTasksList(category: String, status: String) {
        taskcategory = category
        taskstatus = status
        cachetask = Cache(mContext, this)
        cachetask.loadarraylist(CacheCategory.ArrayTask, Task::class)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getDueNextTask() {
        cachetask = Cache(mContext, this)
        cachetask.loadsingleitem(CacheCategory.SingleTask, Task::class)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getTasksJournal() {
        cachetaskjournal = Cache(mContext, this)
        cachetaskjournal.loadarraylist(CacheCategory.TaskJournal, TaskJournal::class)
    }

    private fun filtertasklist(arrayList: ArrayList<Task>): java.util.ArrayList<Task> {
        // The below code returns an ArrayList based on the Category and Status requested
        var tasklist = java.util.ArrayList<Task>()
        for (taskitem in arrayList) {
            if (taskitem!!.status == taskstatus) {
                if (taskcategory != "") {
                    if (taskitem!!.category == taskcategory) {
                        tasklist.add(taskitem!!)
                    }
                }
            }
        }
        return tasklist
    }

    // The below section contains the callbacks from the Cache interfaces
    // Callbacks when ArrayList data is obtained from the cache
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onArrayList(arrayList: ArrayList<*>, classtype: String) {
        if (classtype == CacheCategory.TaskJournal.classtype) {
            // I'm getting an arraylist of TaskJournal from the Cache
            when (activefragment) {
                "DA" -> fragmentDA.onTaskJournal(
                    inflatedView,
                    arrayList as ArrayList<TaskJournal>
                )
            }
        } else if (classtype == CacheCategory.SingleTask.classtype) {
            // I'm getting an arraylist of Tasks from the Cache
            // Cache stores a complete list of Tasks in the application
            var tasklist = filtertasklist(arrayList as ArrayList<Task>)

            when (activefragment) {
                "TT" -> fragmentTT.onTaskList(
                    inflatedView,
                    taskstatus,
                    tasklist
                )
            }
        }
    }

    override fun onEmptyList(classtype: String) {
        //Class type is being used to identify what kind of data I'm not getting back from the cache
        if (classtype == CacheCategory.TaskJournal.classtype) {
            val task = TaskModel()
            task.getAllTasksList(
                userid,
                eventid,
                object : TaskModel.FirebaseSuccessTaskList {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onTaskList(list: ArrayList<Task>) {
                        if (list.isNotEmpty()) {
                            // Converting a Task list into a TaskJournal list
                            // Unique Dates Array
                            var taskdatelist: ArrayList<String> = ArrayList()
                            for (task in list) {
                                if (!taskdatelist.contains(task.date)) {
                                    taskdatelist.add(task.date)
                                }
                            }
                            // Araylist of Tasks when the Date is the Key
                            var taskjournal: ArrayList<TaskJournal> = ArrayList()
                            var taskjournallist: ArrayList<Task> = ArrayList()
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
                            Collections.sort(taskjournal,
                                Comparator { o1, o2 ->
                                    if (o1.date == null || o2.date == null) 0 else o1.date
                                        .compareTo(o2.date)
                                })

                            // Saving my TaskJournal list into the cache
                            cachetaskjournal.save(CacheCategory.TaskJournal, taskjournal)

                            when (activefragment) {
                                "DA" -> fragmentDA.onTaskJournal(
                                    inflatedView,
                                    taskjournal
                                )
                            }
                        } else {
                            // In case I don't have tasks at all. This is notified to the View
                            when (activefragment) {
                                "DA" -> fragmentDA.onTaskJournalError(
                                    inflatedView,
                                    "NO_TASKS"
                                )
                            }
                        }
                    }
                })
        } else if (classtype == CacheCategory.SingleTask.classtype) {
            // This is when I receive an empty list of Tasks from the cache
            val task = TaskModel()
            task.getAllTasksList(
                userid,
                eventid,
                object : TaskModel.FirebaseSuccessTaskList {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onTaskList(arrayList: ArrayList<Task>) {
                        if (arrayList.isNotEmpty()) {
                            // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                            cachetask.save(CacheCategory.ArrayTask, arrayList)

                            // At this point we are filtering the data obtained from Firebase as it's being requested by the app
                            var tasklist = filtertasklist(arrayList)
                            // And sending it back to the view
                            when (activefragment) {
                                "TT" -> fragmentTT.onTaskList(
                                    inflatedView,
                                    taskstatus,
                                    tasklist
                                )
                            }
                        } else {
                            // This is when there is no data coming from Firebase
                            when (activefragment) {
                                "TT" -> fragmentTT.onTaskListError(
                                    inflatedView,
                                    "NO_TASKS"
                                )
                            }
                        }
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSingleItem(item: Any) {
        if (item is Task) {
            when (activefragment) {
                "DE" -> fragmentDE.onTask(
                    inflatedView,
                    item
                )
            }
        }
    }

    override fun onEmptyItem(classtype: String) {
        if (classtype == CacheCategory.SingleTask.classtype) {
            // This is only catching when there is not a Task coming, if more data types are needed,
            // I'd may need to control it by the messages
            val task = TaskModel()
            task.getDueNextTask(userid, eventid, object : TaskModel.FirebaseSuccessTask {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onTask(task: Task) {
                    cachetask.save(CacheCategory.SingleTask, task)
                    if (task.key == "") {
                        when (activefragment) {
                            "DE" -> fragmentDE.onTaskError(inflatedView, "BLANK_TASK")
                        }
                    } else {
                        when (activefragment) {
                            "DE" -> fragmentDE.onTask(inflatedView, task)
                        }
                    }
                }
            })
        }
    }

    interface TaskStats {
        fun onTasksStats(
            inflatedView: View,
            taskpending: Int,
            taskcompleted: Int,
            sumbudget: Float
        )

        fun onTaskStatsError(inflatedView: View, errcode: String)
    }

    interface TaskItem {
        fun onTask(inflatedView: View, task: Task)
        fun onTaskError(inflatedView: View, errcode: String)
    }

    interface TaskList {
        fun onTaskList(
            inflatedView: View,
            status: String,
            list: ArrayList<Task>
        )

        fun onTaskListError(
            inflatedView: View,
            errcode: String
        )
    }

    interface TaskDateList {
        fun onTaskJournal(
            inflatedView: View,
            list: ArrayList<TaskJournal>
        )

        fun onTaskJournalError(
            inflatedView: View,
            errcode: String
        )
    }
}




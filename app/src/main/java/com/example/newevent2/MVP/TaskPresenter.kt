package com.example.newevent2.MVP

import Application.Cache
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import kotlin.collections.ArrayList

class TaskPresenter : Cache.TaskArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentDE: DashboardEventPresenter
    private lateinit var fragmentDA: DashboardActivityPresenter
    private lateinit var fragmentTPT: TaskPaymentTasksPresenter

    private lateinit var cachetask: Cache<Task>

    constructor(context: Context, fragment: DashboardEventPresenter) {
        fragmentDE = fragment
        mContext = context
        activefragment = "DE"
    }

    constructor(context: Context, fragment: DashboardActivityPresenter) {
        fragmentDA = fragment
        mContext = context
        activefragment = "DA"
    }

    constructor(context: Context, fragment: TaskPaymentTasksPresenter) {
        fragmentTPT = fragment
        mContext = context
        activefragment = "TPT"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getTasksList() {
        cachetask = Cache(mContext, this)
        cachetask.loadarraylist(Task::class)
    }

    // The below section contains the callbacks from the Cache interfaces
    // Callbacks when ArrayList data is obtained from the cache
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onArrayListT(arrayList: ArrayList<Task>) {
        if (arrayList.size == 0) {
            when (activefragment) {
                "DE" -> fragmentDE.onTaskListError(ERRCODETASKS)
                "DA" -> fragmentDA.onTaskListError(ERRCODETASKS)
                "TPT" -> fragmentTPT.onTaskListError(ERRCODETASKS)
            }
        } else {
            when (activefragment) {
                "DE" -> fragmentDE.onTaskList(arrayList)
                "DA" -> fragmentDA.onTaskList(arrayList)
                "TPT" -> fragmentTPT.onTaskList(arrayList)
            }
        }
    }

    override fun onEmptyListT() {
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        // This is when I receive an empty list of Tasks from the cache
        val task = TaskModel()
        task.getAllTasksList(
            user.key,
            user.eventid,
            object : TaskModel.FirebaseSuccessTaskList {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onTaskList(arrayList: ArrayList<Task>) {
                    if (arrayList.isNotEmpty()) {
                        // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                        cachetask.save(arrayList)

                        when (activefragment) {
                            "DE" -> fragmentDE.onTaskList(arrayList)
                            "DA" -> fragmentDA.onTaskList(arrayList)
                            "TPT" -> fragmentTPT.onTaskList(arrayList)
                        }
                    } else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "DE" -> fragmentDE.onTaskListError(ERRCODETASKS)
                            "DA" -> fragmentDA.onTaskListError(ERRCODETASKS)
                            "TPT" -> fragmentTPT.onTaskListError(ERRCODETASKS)
                        }
                    }
                }
            })
    }

    interface TaskList {
        fun onTaskList(list: ArrayList<Task>)
        fun onTaskListError(errcode: String)
    }

    companion object {
        const val ERRCODETASKS = "NOTASKS"
    }
}




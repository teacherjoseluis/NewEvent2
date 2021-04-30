package com.example.newevent2

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import java.text.DecimalFormat

class TaskPayment_Tasks : Fragment(), TaskPresenter.ViewTaskList, TaskPresenter.ViewTaskFragment {

    private var userid: String = ""
    private var eventid: String = ""
    private var category: String = ""

    private lateinit var presentertask: TaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()
        category = this.arguments!!.get("category").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)

        presentertask = TaskPresenter(this, inf, userid, eventid)
        presentertask.getTaskStats(category)
        presentertask.getTasksList(category, ACTIVETASK) // Active
        presentertask.getTasksList(category, COMPLETETASK) // Completed

        return inf
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewTaskListFragment(
        inflatedView: View,
        category: String,
        status: String,
        list: java.util.ArrayList<com.example.newevent2.Model.Task>
    ) {
        if (status == ACTIVETASK) {
            val recyclerViewActive = inflatedView.ActiveTasksRecyclerView
            recyclerViewActive.apply {
                layoutManager = LinearLayoutManager(inflatedView.context).apply {
                    stackFromEnd = true
                    reverseLayout = true
                }
            }
            val rvAdapter = Rv_TaskAdapter(userid, eventid, list)
            recyclerViewActive.adapter = rvAdapter

            val swipeController = SwipeControllerTasks(
                inflatedView.context,
                rvAdapter,
                recyclerViewActive,
                LEFTACTIONACTIVE,
                RIGHTACTIONACTIVE
            )
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recyclerViewActive)
        } else if (status == COMPLETETASK) {
            val recyclerViewComplete = inflatedView.CompleteTasksRecyclerView
            recyclerViewComplete.apply {
                layoutManager = LinearLayoutManager(inflatedView.context).apply {
                    stackFromEnd = true
                    reverseLayout = true
                }
            }
            val rvAdapter = Rv_TaskAdapter(userid, eventid, list)
            Log.d("Link to Task Detail", "TaskPayment_Tasks")
            recyclerViewComplete.adapter = rvAdapter

            val swipeController =
                SwipeControllerTasks(
                    inflatedView.context,
                    rvAdapter,
                    recyclerViewComplete,
                    LEFTACTIONCOMPLETED,
                    RIGHTACTIONCOMPLETED
                )
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
        }
    }

    override fun onViewTaskListErrorFragment(
        inflatedView: View,
        category: String,
        status: String,
        errcode: String
    ) {
        TODO("Not yet implemented")
        // What to show when the consulted category has no tasks?
    }

    override fun onViewTaskStatsSuccessFragment(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float
    ) {
        inflatedView.activetasks.text = taskpending.toString()
        inflatedView.completedtasks.text = taskcompleted.toString()
        val formatter = DecimalFormat("$#,###.00")
        inflatedView.budgettasks.text = formatter.format(sumbudget)
    }

    override fun onViewTaskErrorFragment(inflatedView: View, errcode: String) {
        TODO("Not yet implemented")
        // What to show when the consulted category has no tasks?
    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val LEFTACTIONCOMPLETED = ""
        const val RIGHTACTIONCOMPLETED = "undo"
    }
}
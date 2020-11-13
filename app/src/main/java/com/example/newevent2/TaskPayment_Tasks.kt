package com.example.newevent2

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import java.text.DecimalFormat

class TaskPayment_Tasks : Fragment() {
    private var eventkey: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        category = this.arguments!!.get("category").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)

        val recyclerViewActive = inf.ActiveTasksRecyclerView
        val recyclerViewComplete = inf.CompleteTasksRecyclerView

        recyclerViewActive.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        recyclerViewComplete.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val taskentity = TaskEntity()
        taskentity.eventid = eventkey
        taskentity.category = category
        taskentity.status = "A"

        taskentity.getTasksList(object : FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTasksList(list: ArrayList<Task>) {
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewActive.adapter = rvAdapter

                val swipeController = SwipeControllerTasks(
                    inf.context,
                    rvAdapter,
                    recyclerViewActive,
                    "check",
                    "delete"
                )
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerViewActive)
            }
        })

        taskentity.getTaskStats(object: FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                inf.activetasks.text = taskpending.toString()
                inf.completedtasks.text = taskcompleted.toString()
                val formatter = DecimalFormat("$#,###.00")
                inf.budgettasks.text = formatter.format(sumbudget)
            }

            override fun onTasksList(list: ArrayList<Task>) {
                TODO("Not yet implemented")
            }
        })

        //---------------------------------------------------------------------------------------

        val taskentitycomplete = TaskEntity()
        taskentitycomplete.eventid = eventkey
        taskentitycomplete.category = category
        taskentitycomplete.status = "C"

        taskentitycomplete.getTasksList(object : FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTasksList(list: ArrayList<Task>) {
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewComplete.adapter = rvAdapter

                val swipeController =
                    SwipeControllerTasks(inf.context, rvAdapter, recyclerViewComplete, null, "undo")
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
            }
        })
        return inf
    }
}
package com.example.newevent2

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.Model.Task
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.scrollview
import java.text.DecimalFormat

class TaskPayment_Tasks : Fragment()/*, TaskPresenter.TaskList*/ {

    private var userid: String = ""
    private var eventid: String = ""
    private var category: String = ""

    //private lateinit var presentertask: TaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()
        category = this.arguments!!.get("category").toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)

        //presentertask = TaskPresenter(context!!,this, inf)
        //presentertask.userid=userid
        //presentertask.eventid=eventid
        //presentertask.getTaskStats(category)
        //presentertask.getTasksList(category, ACTIVETASK) // Active
        //presentertask.getTasksList(category, COMPLETETASK) // Completed

        return inf
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    override fun onTaskList(
//        inflatedView: View,
////        category: String,
//        status: String,
//        list: java.util.ArrayList<com.example.newevent2.Model.Task>
//    ) {
//        if (status == ACTIVETASK) {
//            val recyclerViewActive = inflatedView.ActiveTasksRecyclerView
//            recyclerViewActive.apply {
//                layoutManager = LinearLayoutManager(inflatedView.context).apply {
//                    stackFromEnd = true
//                    reverseLayout = true
//                }
//            }
//            val rvAdapter = Rv_TaskAdapter(userid, eventid, list)
//            recyclerViewActive.adapter = rvAdapter
//
//            val swipeController = SwipeControllerTasks(
//                inflatedView.context,
//                rvAdapter,
//                recyclerViewActive,
//                LEFTACTIONACTIVE,
//                RIGHTACTIONACTIVE
//            )
//            val itemTouchHelper = ItemTouchHelper(swipeController)
//            itemTouchHelper.attachToRecyclerView(recyclerViewActive)
//        }
////        else if (status == COMPLETETASK) {
////            val recyclerViewComplete = inflatedView.CompleteTasksRecyclerView
////            recyclerViewComplete.apply {
////                layoutManager = LinearLayoutManager(inflatedView.context).apply {
////                    stackFromEnd = true
////                    reverseLayout = true
////                }
////            }
////            val rvAdapter = Rv_TaskAdapter(userid, eventid, list)
////            Log.d("Link to Task Detail", "TaskPayment_Tasks")
////            recyclerViewComplete.adapter = rvAdapter
////
////            val swipeController =
////                SwipeControllerTasks(
////                    inflatedView.context,
////                    rvAdapter,
////                    recyclerViewComplete,
////                    LEFTACTIONCOMPLETED,
////                    RIGHTACTIONCOMPLETED
////                )
////            val itemTouchHelper = ItemTouchHelper(swipeController)
////            itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
////        }
//    }

//    override fun onTaskListError(
//        inflatedView: View,
//        errcode: String
//    ) {
//            inflatedView.withnodatataskpayment.visibility = ConstraintLayout.VISIBLE
//            inflatedView.scrollview.visibility = ConstraintLayout.GONE
//    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val LEFTACTIONCOMPLETED = ""
        const val RIGHTACTIONCOMPLETED = "undo"
    }
}
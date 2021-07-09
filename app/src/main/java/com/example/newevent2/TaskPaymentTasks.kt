package com.example.newevent2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.TaskPaymentTasksPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.scrollviewt

class TaskPaymentTasks : Fragment(), TaskPaymentTasksPresenter.TPTasks {

    private var category: String = ""
    private var status: String = ""
    private lateinit var presentertask: TaskPaymentTasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = if (this.arguments!!.get("category") != null) {
            this.arguments!!.get("category").toString()
        } else {
            ""
        }
        status = this.arguments!!.get("status").toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)
        presentertask = TaskPaymentTasksPresenter(context!!, this, inf, category, status)
        return inf
    }

    override fun onTPTasks(
        inflatedView: View,
        list: ArrayList<Task>
    ) {
        if (list.size != 0) {
            if (status == TaskModel.ACTIVESTATUS) {
                val recyclerViewActive = inflatedView.ActiveTasksRecyclerView
                recyclerViewActive.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewActive.adapter = rvAdapter

                val swipeController = SwipeControllerTasks(
                    inflatedView.context,
                    rvAdapter,
                    recyclerViewActive,
                    LEFTACTIONACTIVE,
                    RIGHTACTIONACTIVE
                )
                val itemTouchHelperA = ItemTouchHelper(swipeController)
                itemTouchHelperA.attachToRecyclerView(recyclerViewActive)
            } else if (status == TaskModel.COMPLETESTATUS) {
                val recyclerViewComplete = inflatedView.ActiveTasksRecyclerView
                recyclerViewComplete.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewComplete.adapter = rvAdapter

                val swipeControllerC =
                    SwipeControllerTasks(
                        inflatedView.context,
                        rvAdapter,
                        recyclerViewComplete,
                        null,
                        RIGHTACTIONCOMPLETED
                    )
                val itemTouchHelper = ItemTouchHelper(swipeControllerC)
                itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
            }
        } else if (list.size == 0) {
            inflatedView.activetaskslayout.visibility = ConstraintLayout.INVISIBLE
        }
    }

    override fun onTPTasksError(inflatedView: View, errcode: String) {
        inflatedView.withnodatataskpaymentt.visibility = ConstraintLayout.VISIBLE
        inflatedView.scrollviewt.visibility = ConstraintLayout.GONE
    }

    companion object {
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val LEFTACTIONCOMPLETED = ""
        const val RIGHTACTIONCOMPLETED = "undo"
    }
}
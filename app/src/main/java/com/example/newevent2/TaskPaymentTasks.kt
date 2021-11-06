package com.example.newevent2

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

    private lateinit var presentertask: TaskPaymentTasksPresenter
    private var category: String = ""
    private var status: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //If there is a category sent as parameter, then the class will return a list of tasks
        // matching the category, otherwise it will send all of the Tasks available
        category = if (this.requireArguments().get("category") != null) {
            this.requireArguments().get("category").toString()
        } else {
            ""
        }
        status = this.requireArguments().get("status").toString()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)
        //Calling the presenter
        presentertask = TaskPaymentTasksPresenter(requireContext(), this, inf, category, status)
        return inf
    }

    override fun onTPTasks(
        inflatedView: View,
        list: ArrayList<Task>
    ) {
        if (list.size != 0) {
            // Tasks are retrieved from the presenter
            if (status == TaskModel.ACTIVESTATUS) {
                //Tasks are Active and associated to an Active Recyclerview
                val recyclerViewActive = inflatedView.ActiveTasksRecyclerView
                recyclerViewActive.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewActive.adapter = rvAdapter

                // Both left and right swipe are enabled for this particular recyclerview
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
                //These are completed tasks that are associated to this recyclerview
                val recyclerViewComplete = inflatedView.ActiveTasksRecyclerView
                recyclerViewComplete.apply {
                    layoutManager = LinearLayoutManager(inflatedView.context).apply {
                        stackFromEnd = true
                        reverseLayout = true
                    }
                }
                val rvAdapter = Rv_TaskAdapter(list)
                recyclerViewComplete.adapter = rvAdapter

                //Only right behavior is allowed for the Swipe
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
            //If no tasks are retrieved from the presenter the component is marked as invisible
            inflatedView.activetaskslayout.visibility = ConstraintLayout.INVISIBLE
        }
    }

    override fun onTPTasksError(inflatedView: View, errcode: String) {
        //We are showing the empty state layout and hiding the one that will load with task data
        inflatedView.withnodatataskpaymentt.visibility = ConstraintLayout.VISIBLE
        inflatedView.scrollviewt.visibility = ConstraintLayout.GONE
    }

    companion object {
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val RIGHTACTIONCOMPLETED = "undo"
    }
}
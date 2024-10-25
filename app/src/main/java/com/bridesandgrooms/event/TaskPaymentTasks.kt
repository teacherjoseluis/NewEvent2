package com.bridesandgrooms.event

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bridesandgrooms.event.MVP.TaskPaymentTasksPresenter
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskModel
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.UI.Adapters.ItemSwipeListenerTask
import com.bridesandgrooms.event.UI.Adapters.TaskAdapter
import com.bridesandgrooms.event.UI.Fragments.TaskCreateEdit
import com.bridesandgrooms.event.databinding.TaskpaymentTasksBinding
import com.bridesandgrooms.event.UI.SwipeControllerTasks
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskPaymentTasks : Fragment(), TaskPaymentTasksPresenter.TPTasks, ItemSwipeListenerTask,
    TPT_TaskFragmentActionListener {

    private lateinit var recyclerViewActive: RecyclerView
    private lateinit var recyclerViewComplete: RecyclerView

    private lateinit var presentertask: TaskPaymentTasksPresenter
    private var category: String = ""
    private var status: String = ""
    private lateinit var inf: TaskpaymentTasksBinding
    private lateinit var rvAdapter: TaskAdapter

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = if (arguments?.get("category") != null) {
            this.requireArguments().get("category").toString()
        } else {
            ""
        }
        status = this.requireArguments().get("status").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inf = DataBindingUtil.inflate(inflater, R.layout.taskpayment_tasks, container, false)
        try {
            presentertask = TaskPaymentTasksPresenter(mContext!!, this, category, status)
            presentertask.getTaskList()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return inf.root
    }

    override fun onTPTasks(
        list: ArrayList<Task>
    ) {
        if (list.size > 0) {
            recyclerViewActive = inf.ActiveTasksRecyclerView
            recyclerViewActive.apply {
                layoutManager = LinearLayoutManager(inf.root.context).apply {
                    stackFromEnd = true
                    reverseLayout = true
                }
            }

            rvAdapter = TaskAdapter(this, list, this, mContext!!)
            recyclerViewActive.adapter = null
            recyclerViewActive.adapter = rvAdapter

            if (status == TaskModel.ACTIVESTATUS) {
                val swipeController = SwipeControllerTasks(
                    inf.root.context,
                    rvAdapter,
                    recyclerViewActive,
                    LEFTACTIONACTIVE,
                    RIGHTACTIONACTIVE
                )
                val itemTouchHelperA = ItemTouchHelper(swipeController)
                itemTouchHelperA.attachToRecyclerView(recyclerViewActive)
            } else if (status == TaskModel.COMPLETESTATUS) {
                val swipeControllerC =
                    SwipeControllerTasks(
                        inf.root.context,
                        rvAdapter,
                        recyclerViewComplete,
                        null,
                        RIGHTACTIONCOMPLETED
                    )
                val itemTouchHelper = ItemTouchHelper(swipeControllerC)
                itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
            }
        } else {
            emptyStateFragment()
        }
    }

    override fun onTPTasksError(errcode: String) {
        emptyStateFragment()
    }

    fun emptyStateFragment() {
        val container = view as ViewGroup?
        container?.removeAllViews()

        val newView = layoutInflater.inflate(R.layout.empty_state_fragment, container, false)
        container?.addView(newView)

        newView.findViewById<TextView>(R.id.emptystate_message).setText(R.string.emptystate_notasksmsg)
        newView.findViewById<TextView>(R.id.emptystate_cta).setText(R.string.emptystate_notasksscta)
        newView.findViewById<FloatingActionButton>(R.id.fab_action).setOnClickListener {
            callTaskCreateFragment()
        }
    }

    companion object {
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val RIGHTACTIONCOMPLETED = "undo"
        const val TAG = "TaskPaymentTasks"
    }

    override fun onItemSwiped(taskList: MutableList<Task>) {
        if (taskList.isEmpty()) {
            emptyStateFragment()
        } else {
            //----------------------------------------------------------------
            inf.activetaskslayout.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.withnodatataskpaymentt
            emptystateLayout.root.visibility = ConstraintLayout.GONE
            //----------------------------------------------------------------
        }
    }

    override fun onTaskFragmentWithData(task: Task) {
        val fragment = TaskCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("task", task)
        bundle.putString("calling_fragment", "TaskPaymentTasks")
        //------------------------------------------------------
        bundle.putString("category", category)
        bundle.putString("status", status)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    fun callTaskCreateFragment(){
        val fragment = TaskCreateEdit()
        val bundle = Bundle()
        bundle.putString("calling_fragment", "EmptyState")
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }
}

interface TPT_TaskFragmentActionListener {
    fun onTaskFragmentWithData(task: Task)
}
package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bridesandgrooms.event.MVP.TaskPaymentTasksPresenter
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*


class TaskPaymentTasks : Fragment(), TaskPaymentTasksPresenter.TPTasks {

    private lateinit var recyclerViewActive: RecyclerView
    private lateinit var recyclerViewComplete: RecyclerView

    private lateinit var presentertask: TaskPaymentTasksPresenter
    private var category: String = ""
    private var status: String = ""
    private lateinit var inf: View
    private lateinit var rvAdapter: Rv_TaskAdapter

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
    ): View {
        inf = inflater.inflate(R.layout.taskpayment_tasks, container, false)
        //Calling the presenter

//        val pulltoRefresh = inf.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
//        pulltoRefresh.setOnRefreshListener {
//            presentertask = TaskPaymentTasksPresenter(requireContext(), this, inf, category, status)
//            pullToRefresh.isRefreshing = false
//        }

        //Tasks are Active and associated to an Active Recyclerview
        recyclerViewActive = inf.ActiveTasksRecyclerView
        recyclerViewActive.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        //These are completed tasks that are associated to this recyclerview
        recyclerViewComplete = inf.ActiveTasksRecyclerView
        recyclerViewComplete.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        try {
            presentertask = TaskPaymentTasksPresenter(requireContext(), this, inf, category, status)
        } catch (e: Exception) {
            println(e.message)
        }
        return inf
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTPTasks(
        inflatedView: View,
        list: ArrayList<Task>
    ) {
        if (list.size != 0) {
            // Tasks are retrieved from the presenter
            if (status == TaskModel.ACTIVESTATUS) {
                rvAdapter = Rv_TaskAdapter(list)
                recyclerViewActive.adapter = null
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
                rvAdapter = Rv_TaskAdapter(list)
                recyclerViewComplete.adapter = null
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
            inflatedView.activetaskslayout.visibility = ConstraintLayout.GONE

            val emptystateLayout = inflatedView.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentt)
            val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
            val bottomMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
            val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams

            params.topMargin = topMarginInPixels
            params.bottomMargin = bottomMarginInPixels
            emptystateLayout.layoutParams = params
            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_notasksmsg)

            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)

            emptystateLayout.newtaskbutton.setOnClickListener {
                val newTask = Intent(context, TaskCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        }
    }

    override fun onTPTasksError(inflatedView: View, errcode: String) {
        //We are showing the empty state layout and hiding the one that will load with task data
        inflatedView.withnodatataskpaymentt.visibility = ConstraintLayout.GONE

        val emptystateLayout = inflatedView.findViewById<ConstraintLayout>(R.id.withnodatataskpaymentt)
        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
        val bottomMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
        val params = emptystateLayout.layoutParams as ViewGroup.MarginLayoutParams

        params.topMargin = topMarginInPixels
        params.bottomMargin = bottomMarginInPixels
        emptystateLayout.layoutParams = params
        emptystateLayout.visibility = ConstraintLayout.VISIBLE
        emptystateLayout.empty_card.onboardingmessage.text =
            getString(R.string.emptystate_notasksmsg)

        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
        emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)

        emptystateLayout.newtaskbutton.setOnClickListener {
            val newTask = Intent(context, TaskCreateEdit::class.java)
            newTask.putExtra("userid", "")
            startActivity(newTask)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presentertask = TaskPaymentTasksPresenter(requireContext(), this, inf, category, status)
        } catch (e: Exception) {
            println(e.message)
        }
//        recyclerViewActive.adapter = null
//        recyclerViewActive.adapter = rvAdapter
    }

    companion object {
        const val LEFTACTIONACTIVE = "check"
        const val RIGHTACTIONACTIVE = "delete"
        const val RIGHTACTIONCOMPLETED = "undo"
    }
}
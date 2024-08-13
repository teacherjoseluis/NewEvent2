package com.bridesandgrooms.event.UI.Fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bridesandgrooms.event.MVP.TasksPaymentAllCalendarPresenter
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TaskCreateEdit
import com.bridesandgrooms.event.UI.Adapters.TaskCalendarAdapter
import com.bridesandgrooms.event.databinding.TasksAllCalendarBinding
import com.google.android.material.appbar.MaterialToolbar
import java.util.*
import kotlin.collections.ArrayList

class TasksAllCalendar : Fragment(),
    TasksPaymentAllCalendarPresenter.TaskArrayInterface, TaskFragmentActionListener {

    private lateinit var recyclerViewAllTasksCalendar: RecyclerView
    private lateinit var presentertask: TasksPaymentAllCalendarPresenter
    private lateinit var rvAdapter: TaskCalendarAdapter
    private lateinit var inf: TasksAllCalendarBinding
    private lateinit var toolbar: MaterialToolbar

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.tasks)

        inf = DataBindingUtil.inflate(inflater, R.layout.tasks_all_calendar, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.apply{
            setHomeAsUpIndicator(R.drawable.icons8_left_24)
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        recyclerViewAllTasksCalendar = inf.recyclerViewTasksCalendar
        recyclerViewAllTasksCalendar.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        val arguments = arguments
        if (arguments != null && arguments.containsKey("taskDate")) {
            val taskDateMillis = arguments.getLong("taskDate")
            val taskDate = Date(taskDateMillis)

            try {
                presentertask = TasksPaymentAllCalendarPresenter(mContext!!, this, null)
                presentertask.getDateTaskArray(taskDate)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
        return inf.root
    }

    companion object {
        const val SCREEN_NAME = "Tasks All Calendar"
        const val TAG = "TasksAllCalendar"
    }

    override fun onTaskArray(list: ArrayList<Task>?) {
        if (list != null) {
            if (list.size != 0) {
                try {
                    rvAdapter = TaskCalendarAdapter(this, list, mContext!!)
                    rvAdapter.notifyDataSetChanged()
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, e.message.toString())
                }

                recyclerViewAllTasksCalendar.adapter = null
                recyclerViewAllTasksCalendar.adapter = rvAdapter
            }
        }
    }

    override fun onTaskArrayError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setHomeAsUpIndicator(null)
            setDisplayHomeAsUpEnabled(false)
        }

        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerlayout)
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            0,
            0
        )
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onTaskFragmentWithData(task: Task) {
        val fragment = TaskCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("task", task)
        bundle.putString("calling_fragment", "TasksAllCalendar")
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .commit()
    }
}

interface TaskFragmentActionListener {
    fun onTaskFragmentWithData(task: Task)
}

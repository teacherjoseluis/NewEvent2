package com.example.newevent2

import android.icu.text.DateFormat.DAY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import kotlinx.android.synthetic.main.welcome.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DashboardActivity() : Fragment(), TaskPresenter.TaskDateList {

    lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presentertask: TaskPresenter

    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments!!.get("userid").toString()
        eventid = arguments!!.get("eventid").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.dashboardactivity, container, false)

        recyclerViewActivity = inf.journalparentrv
        recyclerViewActivity.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        presentertask = TaskPresenter(context!!,this, inf)
        presentertask.userid = userid
        presentertask.eventid = eventid
        presentertask.getTasksJournal()
        return inf
    }

    override fun onTaskJournal(inflatedView: View, list: ArrayList<TaskJournal>) {
        //emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_TaskDatesAdapter(userid, eventid, list)
        recyclerViewActivity.adapter = rvAdapter
    }

    override fun onTaskJournalError(inflatedView: View, errcode: String) {
        journalparentrv.visibility = View.GONE
    }

}



package com.example.newevent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.MVP.LogPresenter
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import kotlinx.android.synthetic.main.welcome.recentactivityrv

class DashboardActivity(private val view: DashboardView) : Fragment(), LogPresenter.ViewLogActivity {

    lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presenter: LogPresenter

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

        recyclerViewActivity = inf.recentactivityrv
        recyclerViewActivity.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        presenter = LogPresenter(this, userid, eventid)
        return inf
    }

    override fun onViewLogSuccess(loglist: ArrayList<Loginfo>) {
        emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_LogAdapter(loglist)
        recentactivityrv.adapter = rvAdapter
    }

    override fun onViewLogError(errcode: String) {
        recentactivityrv.visibility = View.GONE
    }
}
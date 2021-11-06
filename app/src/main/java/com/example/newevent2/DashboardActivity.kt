package com.example.newevent2

//import com.example.newevent2.MVP.TaskPresenter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.MVP.DashboardActivityPresenter
import com.example.newevent2.Model.TaskJournal
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*

class DashboardActivity : Fragment(), DashboardActivityPresenter.TaskJournalInterface {

    private lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presentertask: DashboardActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                isNestedScrollingEnabled = false
            }
        }
        inf.NewTaskActionButton.setOnClickListener {
            val newtask = Intent(activity, TaskCreateEdit::class.java)
            newtask.putExtra("userid", "")
//            newtask.putExtra("eventid", userSession.eventid)
            startActivity(newtask)
        }
        presentertask = DashboardActivityPresenter(requireContext(), this, inf)
        return inf
    }

    override fun onTaskJournal(inflatedView: View, list: ArrayList<TaskJournal>) {
        //emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_TaskDatesAdapter(list)
        recyclerViewActivity.adapter = rvAdapter
    }

    override fun onTaskJournalError(inflatedView: View, errcode: String) {
        //Consider adding a try catch in case there is no data coming from Firebase
        journalparentrv.visibility = View.GONE
        noactivity.visibility = View.VISIBLE
    }
}



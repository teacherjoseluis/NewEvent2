package com.bridesandgrooms.event

//import com.example.newevent2.MVP.TaskPresenter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.MVP.DashboardActivityPresenter
import com.bridesandgrooms.event.Model.TaskJournal
import com.bridesandgrooms.event.UI.ExportPDF
import com.bridesandgrooms.event.databinding.DashboardactivityBinding

//import kotlinx.android.synthetic.main.dashboardactivity.*
//import kotlinx.android.synthetic.main.dashboardactivity.view.*
//import kotlinx.android.synthetic.main.onboardingcard.view.*

class DashboardActivity : Fragment(), DashboardActivityPresenter.TaskJournalInterface {

    private lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presentertask: DashboardActivityPresenter
    private lateinit var inf: DashboardactivityBinding
    private lateinit var activitymenu: Menu

    private val REQUEST_CODE_TASK = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        //val inf = inflater.inflate(R.layout.dashboardactivity, container, false)
        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardactivity, container, false)
        recyclerViewActivity = inf.journalparentrv
        recyclerViewActivity.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
                isNestedScrollingEnabled = false
            }
        }
        inf.NewTaskActionButton.setOnClickListener {
            val newtask = Intent(activity, TaskCreateEdit::class.java)
            newtask.putExtra("userid", "")
//            newtask.putExtra("eventid", userSession.eventid)
            startActivityForResult(newtask, REQUEST_CODE_TASK)
        }
        try {
            presentertask = DashboardActivityPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }
        return inf.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tasklist_menu, menu)
        activitymenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export_task -> {
                val exportpdf = Intent(context, ExportPDF::class.java)
                startActivity(exportpdf)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTaskJournal(list: ArrayList<TaskJournal>) {
        //emptyrecyclerview.visibility = View.GONE
        val rvAdapter = Rv_TaskDatesAdapter(list)
        rvAdapter.notifyDataSetChanged()
        recyclerViewActivity.adapter = rvAdapter
    }

    override fun onTaskJournalError(errcode: String) {
        //Consider adding a try catch in case there is no data coming from Firebase
        inf.journalparentrv.visibility = View.GONE
        inf.withnodataj.root.visibility = ConstraintLayout.VISIBLE
        inf.withnodataj.emptyCard.onboardingmessage.text = getString(R.string.emptystate_notasksmsg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE_TASK) && resultCode == Activity.RESULT_OK) {
            try {
                presentertask = DashboardActivityPresenter(requireContext(), this)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presentertask = DashboardActivityPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}



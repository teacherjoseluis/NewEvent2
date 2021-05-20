package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import com.example.newevent2.Model.TaskModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*

class Rv_TaskDatesAdapter(
    val userid: String,
    val eventid: String,
    private val taskjournalList: ArrayList<TaskJournal>
) :
    RecyclerView.Adapter<Rv_TaskDatesAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var recyclerViewActivity: RecyclerView

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.journal_parentlayout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskjournalList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.journaldate?.text = taskjournalList[p1].date
        p0.recyclerView?.apply {
            layoutManager = LinearLayoutManager(p0.recyclerView.context).apply {
                stackFromEnd = true
                reverseLayout = true
                setRecycledViewPool(viewPool)
            }
        }
        val rvAdapter = Rv_TaskAdapter(userid, eventid, taskjournalList[p1].taskjournallist)
        p0.recyclerView!!.adapter = rvAdapter
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journaldate: TextView? = itemView.findViewById<TextView>(R.id.journaldate)
        val recyclerView: RecyclerView? = itemView.findViewById<RecyclerView>(R.id.tasksrv)
    }
}




package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.converttoString
import com.example.newevent2.Functions.getlocale
import com.example.newevent2.Model.TaskJournal
import java.text.DateFormat
import java.util.*
import java.util.Locale.getDefault
import kotlin.collections.ArrayList

class Rv_TaskDatesAdapter(
    private val taskjournalList: ArrayList<TaskJournal>
) : RecyclerView.Adapter<Rv_TaskDatesAdapter.ViewHolder>() {

    lateinit var context: Context
    private val viewPool = RecyclerView.RecycledViewPool()

    private val EVEN = 1
    private val ODD = 2

    //--------------------------------------------------
    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0) {
            return EVEN
        }
        return ODD
    }
    //--------------------------------------------------

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
//        val v = LayoutInflater.from(p0.context).inflate(R.layout.journal_parentlayout, p0, false)
//        context = p0.context
//        return ViewHolder(v)
        lateinit var genericViewHolder: ViewHolder
        when (p1) {
            EVEN -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.journal_parentlayout, p0, false)
                context = p0.context
                genericViewHolder = ViewHolder(v)
            }
            ODD -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.journal_parentlayout2, p0, false)
                context = p0.context
                genericViewHolder = ViewHolder(v)
            }
        }
        return genericViewHolder
    }

    override fun getItemCount(): Int {
        return taskjournalList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val stringdate = converttoString(taskjournalList[p1].date, DateFormat.MEDIUM)
        if(getlocale().substring(0,2) == "es")
        {//Getting the right substring depending the default language
            p0.journaldate?.text =
                stringdate.substring(0, stringdate.lastIndexOf('.')).trim()
        } else {
            p0.journaldate?.text =
                stringdate.substring(0, stringdate.lastIndexOf(',')).trim()
        }

        p0.recyclerView?.apply {
            layoutManager = LinearLayoutManager(p0.recyclerView.context).apply {
                stackFromEnd = true
                reverseLayout = true
//                isNestedScrollingEnabled = false
                setRecycledViewPool(viewPool)
            }
        }
        //val rvAdapter = Rv_TaskJournalAdapter(taskjournalList[p1].taskjournallist)
        val rvAdapter = Rv_TaskAdapter2(taskjournalList[p1].taskjournallist)
        p0.recyclerView!!.adapter = rvAdapter
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journaldate: TextView? = itemView.findViewById(R.id.journaldate)
        val recyclerView: RecyclerView? = itemView.findViewById(R.id.tasksrv)
    }
}




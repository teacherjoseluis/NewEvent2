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
import com.example.newevent2.Functions.converttoString
import com.example.newevent2.Model.TableGuests
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import com.example.newevent2.Model.TaskModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import java.text.DateFormat

class Rv_TableGuestsAdapter(
    val tableguestsList: ArrayList<TableGuests>
) : RecyclerView.Adapter<Rv_TableGuestsAdapter.ViewHolder>() {

    lateinit var context: Context
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.tableguests_parentlayout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tableguestsList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.table?.text = tableguestsList[p1].table
        p0.count?.text = tableguestsList[p1].count.toString()
        p0.recyclerView?.apply {
            layoutManager = LinearLayoutManager(p0.recyclerView.context).apply {
                stackFromEnd = true
                reverseLayout = true
                setRecycledViewPool(viewPool)
            }
        }
        val rvAdapter = Rv_GuestAdapter2(tableguestsList[p1].tableguestlist, context)
        p0.recyclerView!!.adapter = rvAdapter
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val table: TextView? = itemView.findViewById<TextView>(R.id.table)
        val count: TextView? = itemView.findViewById<TextView>(R.id.count)
        val recyclerView: RecyclerView? = itemView.findViewById<RecyclerView>(R.id.guestsrv)
    }
}




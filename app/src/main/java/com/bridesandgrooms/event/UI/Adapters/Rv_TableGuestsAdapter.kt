package com.bridesandgrooms.event.UI.Adapters

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
import com.bridesandgrooms.event.Model.TableGuests
import com.bridesandgrooms.event.R

class Rv_TableGuestsAdapter(
    private val tableguestsList: ArrayList<TableGuests>
) : RecyclerView.Adapter<Rv_TableGuestsAdapter.ViewHolder>() {

    lateinit var context: Context
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(p0.context).inflate(R.layout.tableguests_parentlayout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tableguestsList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.table?.text = when (tableguestsList[p1].table) {
            "family" -> context.getString(R.string.family)
            "extendedfamily" -> context.getString(R.string.extendedfamily)
            "familyfriends" -> context.getString(R.string.familyfriends)
            "oldfriends" -> context.getString(R.string.oldfriends)
            "coworkers" -> context.getString(R.string.coworkers)
            "notattending" -> context.getString(R.string.notattending)
            else -> context.getString(R.string.others)
        }

        p0.count?.text = tableguestsList[p1].count.toString()

        if (p0.recyclerView?.layoutManager == null) {
            p0.recyclerView?.apply {
                layoutManager = LinearLayoutManager(p0.recyclerView.context).apply {
                    stackFromEnd = true
                    reverseLayout = true
                    setRecycledViewPool(viewPool)
                }
            }
        }

        val rvAdapter = p0.recyclerView?.adapter as? GuestAdapter
        if (rvAdapter == null) {
            //val newrvAdapter = GuestAdapter(tableguestsList[p1].tableguestlist, context)
            //p0.recyclerView!!.adapter = newrvAdapter
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val table: TextView? = itemView.findViewById(R.id.table)
        val count: TextView? = itemView.findViewById(R.id.count)
        val recyclerView: RecyclerView? = itemView.findViewById(R.id.guestsrv)
    }
}




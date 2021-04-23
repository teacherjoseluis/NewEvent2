package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.Loginfo

class Rv_LogAdapter(val logList: ArrayList<Loginfo>) :
        RecyclerView.Adapter<Rv_LogAdapter.ViewHolder>() {
        // ViewGroup - Views container

        lateinit var context: Context

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            // Instantiates a layout XML file into its corresponding View objects
            val v = LayoutInflater.from(p0?.context).inflate(R.layout.log_item_layout, p0, false)
            context = p0.context
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return logList.size
        }

        // public abstract void onBindViewHolder (VH holder, int position)
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0.logcreated?.text = logList[p1].createdatetime
            p0.actionlabel?.text = when(logList[p1].action){
                "INSERT" -> "Created"
                "UPDATE" -> "Modified"
                "DELETE" -> "Deleted"
                else -> "Nothing"
            }
            p0.entitylabel?.text = logList[p1].entity
            p0.namelabel?.text = logList[p1].entityname

        }

        // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
        //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val logcreated: TextView? = itemView.findViewById<TextView>(R.id.logcreated)
            val actionlabel: TextView? = itemView.findViewById<TextView>(R.id.actionlabel)
            val entitylabel: TextView? = itemView.findViewById<TextView>(R.id.entitylabel)
            val namelabel: TextView? = itemView.findViewById<TextView>(R.id.namelabel)
        }

    }

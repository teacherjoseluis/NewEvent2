//package com.example.newevent2
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import androidx.recyclerview.widget.RecyclerView
//import com.example.newevent2.Model.Task
//import com.example.newevent2.Model.TaskModel
//import com.google.android.material.snackbar.Snackbar
//
//class Rv_TaskJournalAdapter(val taskList: MutableList<Task>) :
//    RecyclerView.Adapter<Rv_TaskJournalAdapter.ViewHolder>() {
//
//    lateinit var context: Context
//
//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
//        val v = LayoutInflater.from(p0?.context).inflate(R.layout.taskjournal_item_layout, p0, false)
//        context = p0.context
//        return ViewHolder(v)
//    }
//
//    override fun getItemCount(): Int {
//        return taskList.size
//    }
//
//    @SuppressLint("SetTextI18n")
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        p0.taskname?.text = taskList[p1].name
//        p0.taskcategory?.text = taskList[p1].category
//
//        p0.itemView.setOnClickListener {
////            val taskdetail = Intent(context, Task_EditDetail::class.java)
////            taskdetail.putExtra("task", taskList[p1])
////            taskdetail.putExtra("userid", userid)
////            taskdetail.putExtra("eventid", eventid)
////            context.startActivity(taskdetail)
//        }
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
//        val taskcategory: TextView? = itemView.findViewById<TextView>(R.id.taskcategory)
//    }
//}
//
//
//

//package com.example.newevent2
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.snackbar.Snackbar
//
//class Rv_TaskAdapterComplete (val taskList: MutableList<Task>) :
//    RecyclerView.Adapter<Rv_TaskAdapterComplete.ViewHolder>(), ItemTouchAdapterAction {
//
//        lateinit var context: Context
//
//        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
//            // Instantiates a layout XML file into its corresponding View objects
//            val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
//            context = p0.context
//            return ViewHolder(v)
//        }
//
//        override fun getItemCount(): Int {
//            return taskList.size
//        }
//
//        // public abstract void onBindViewHolder (VH holder, int position)
//        @SuppressLint("SetTextI18n")
//        @RequiresApi(Build.VERSION_CODES.O)
//        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//            p0.taskname?.text = taskList[p1].name
//            p0.taskdate?.text = taskList[p1].date
//            p0.taskbudget?.text = taskList[p1].budget
//
//
//            p0.itemView.setOnClickListener {
//                val taskdetail = Intent(context, Task_EditDetail::class.java)
//                taskdetail.putExtra("taskkey", taskList[p1].key)
//                taskdetail.putExtra("eventid", taskList[p1].eventid)
//                taskdetail.putExtra("name", taskList[p1].name)
//                taskdetail.putExtra("date", taskList[p1].date)
//                taskdetail.putExtra("category", taskList[p1].category)
//                taskdetail.putExtra("budget", taskList[p1].budget)
//
//                context.startActivity(taskdetail)
//            }
//        }
//
//        // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
//        //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
//        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
//            val taskdate: TextView? = itemView.findViewById<TextView>(R.id.taskdate)
//            val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudget)
//        }
//
//    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action : String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action : String) {
//            val task = TaskEntity()
//            task.key = taskList[position].key
//            task.eventid = taskList[position].eventid
//            task.name = taskList[position].name
//            task.date = taskList[position].date
//            task.budget = taskList[position].budget
//
//            taskList.removeAt(position)
//            notifyItemRemoved(position)
//            task.editTask("active")
//        }
//
//
//}
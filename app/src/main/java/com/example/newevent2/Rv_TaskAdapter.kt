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
import com.google.android.material.snackbar.Snackbar

// Recyclerview - Displays a scrolling list of elements based on large datasets
// The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
// The adapter creates view holders as needed. The adapter also binds the view holders to their data.
// It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.


class Rv_TaskAdapter(val taskList: MutableList<Task>) :
    RecyclerView.Adapter<Rv_TaskAdapter.ViewHolder>(), ItemTouchHelperAdapter, ItemTouchHelperAdapter2 {
    // ViewGroup - Views container

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
        context = p0.context
        //return ViewHolder(v, context);
        return ViewHolder(v)
    }

//    override fun onCreateSwipeConfiguration(
//        context: Context?,
//        position: Int
//    ): SwipeConfiguration? {
//        return SwipeConfiguration.Builder(context)
//            .setBackgroundColorResource(android.R.color.holo_red_light)
//            .setDrawableResource(android.R.drawable.ic_delete)
//            .build()
//    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.taskname?.text = taskList[p1].name
        p0.taskdate?.text = taskList[p1].date
        p0.taskbudget?.text = taskList[p1].budget

        //var dateformatter = DateFormat.parse("dd / MM / yyyy")

        p0.itemView.setOnClickListener {
            //Toast.makeText(p0.itemView.context, p0.eventname?.text, Toast.LENGTH_SHORT).show()
            //val eventdetail = Intent(context, EventDetail::class.java)
            //eventdetail.putExtra("taskkey", taskList[p1].key)
            //context.startActivity(taskdetail)
        }

    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
        val taskdate: TextView? = itemView.findViewById<TextView>(R.id.taskdate)
        val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudget)
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView) {
        val task = TaskEntity()
        task.key = taskList[position].key
        task.eventid = taskList[position].eventid
        task.name = taskList[position].name
        task.date = taskList[position].date
        task.budget = taskList[position].budget

        taskList.removeAt(position)
        notifyItemRemoved(position)
        task.editTask("complete")

        Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                taskList.add(task)
                notifyItemInserted(taskList.lastIndex)
                task.editTask("active")
            }.show()
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView) {
        val task = TaskEntity()
        task.key = taskList[position].key
        task.eventid = taskList[position].eventid
        task.name = taskList[position].name
        task.date = taskList[position].date
        task.budget = taskList[position].budget
        task.category = taskList[position].category

        taskList.removeAt(position)
        notifyItemRemoved(position)
        task.deleteTask()

        Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                taskList.add(task)
                notifyItemInserted(taskList.lastIndex)
                task.addTask()
            }.show()
    }

    override fun onItemSwiftRight2(position: Int, recyclerView: RecyclerView) {
        val task = TaskEntity()
        task.key = taskList[position].key
        task.eventid = taskList[position].eventid
        task.name = taskList[position].name
        task.date = taskList[position].date
        task.budget = taskList[position].budget

        taskList.removeAt(position)
        notifyItemRemoved(position)
        task.editTask("active")
    }

}




package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Rv_ContactAdapter(val contactlist: MutableList<Contact>) :
    RecyclerView.Adapter<Rv_ContactAdapter.ViewHolder>() {

    lateinit var context: Context
    private var selectedPos = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.contact_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        if (contactlist[p1].imageurl != "") {
            Glide.with(p0.itemView.context)
                .load(contactlist[p1].imageurl)
                .circleCrop()
                //.override(200, 200)
                .into(p0.contactavatar)
        }

//        if (selectedPos == p1) {
//            p0.itemView.isSelected = true
//        }

        p0.contactavatar.setOnClickListener {
            p0.contactavatar.isSelected = (!p0.contactavatar.isSelected)
            //p0.itemView.setBackgroundColor(if (p0.itemView.isSelected) Color.parseColor("#F8BBD0") else Color.parseColor("#FAFAFA"))
            //p0.contactavatar.setBackgroundColor(if (p0.contactavatar.isSelected) Color.parseColor("#7FF8BBD0") else Color.parseColor("#00000000"))
            p0.contactavatar.setColorFilter(if (p0.contactavatar.isSelected) Color.parseColor("#7FC2185B") else Color.parseColor("#00000000"))
        }
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

    }

//    override fun onClick(view: View){
//        notifyItemRangeChanged(selectedPos)
//        selectedPos=getLayoutPosition()
//        notifyItemRangeChanged(selectedPos)
//    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
//class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
    }

//        override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView) {
//            val task = TaskEntity()
//            task.key = taskList[position].key
//            task.eventid = taskList[position].eventid
//            task.name = taskList[position].name
//            task.date = taskList[position].date
//            task.budget = taskList[position].budget
//
//            taskList.removeAt(position)
//            notifyItemRemoved(position)
//            task.editTask("complete")
//
//            Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(task)
//                    notifyItemInserted(taskList.lastIndex)
//                    task.editTask("active")
//                }.show()
//        }

//        override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView) {
//            val task = TaskEntity()
//            task.key = taskList[position].key
//            task.eventid = taskList[position].eventid
//            task.name = taskList[position].name
//            task.date = taskList[position].date
//            task.budget = taskList[position].budget
//            task.category = taskList[position].category
//
//            taskList.removeAt(position)
//            notifyItemRemoved(position)
//            task.deleteTask()
//
//            Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(task)
//                    notifyItemInserted(taskList.lastIndex)
//                    task.addTask()
//                }.show()
//        }
}







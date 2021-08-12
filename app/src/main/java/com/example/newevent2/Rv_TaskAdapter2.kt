package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Category.Companion.getCategory
import com.example.newevent2.Functions.addTask
import com.example.newevent2.Functions.deleteTask
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.task_item_layout.view.*
import kotlinx.android.synthetic.main.task_item_layout2.view.*

class Rv_TaskAdapter2(val taskList: MutableList<Task>) :
    RecyclerView.Adapter<Rv_TaskAdapter2.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context
    var taskmodel = TaskModel()
    lateinit var taskdbhelper: TaskDBHelper
    lateinit var usermodel: UserModel

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout2, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.taskname?.text = taskList[p1].name
        p0.taskname?.setTextColor(getCategory(taskList[p1].category).colorforeground.toColorInt())
        p0.taskcategory?.text = taskList[p1].category
        p0.taskcategory?.setTextColor(getCategory(taskList[p1].category).colorforeground.toColorInt())
        p0.itemView.taskcard.setCardBackgroundColor(getCategory(taskList[p1].category).colorbackground.toColorInt())
//        p0.taskdate?.text = taskList[p1].date
//        p0.taskbudget?.text = taskList[p1].budget
//        val resourceId = context.resources.getIdentifier(getCategory(taskList[p1].category).drawable, "drawable",
//            context.packageName)
//        p0.categoryavatar?.setImageResource(resourceId)

        p0.itemView.setOnClickListener {
            val taskdetail = Intent(context, TaskCreateEdit::class.java)
            taskdetail.putExtra("task", taskList[p1])
            context.startActivity(taskdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
        val taskcategory: TextView? = itemView.findViewById<TextView>(R.id.taskcategory)
//        val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudgets)
//        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        if (action == CHECKACTION) {
            val taskswift = taskList[position]
            taskList.removeAt(position)
            notifyItemRemoved(position)
            taskswift.status = COMPLETETASK
            editTask(context, taskswift)

            val snackbar = Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    taskList.add(taskswift)
                    notifyItemInserted(taskList.lastIndex)
                    taskswift.status = ACTIVETASK
                    editTask(context, taskswift)
                }
            snackbar.show()
        }
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        //val user = com.example.newevent2.Functions.getUserSession(context!!)
        val taskswift = taskList[position]
        val taskbackup = Task().apply {
            name = taskswift.name
            budget = taskswift.budget
            date = taskswift.date
            category = taskswift.category
            status = taskswift.status
            createdatetime = taskswift.createdatetime
        }

        if (action == DELETEACTION) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
            deleteTask(context, taskswift)

            val snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    taskList.add(taskswift)
                    notifyItemInserted(taskList.lastIndex)
                    taskswift.status = ACTIVETASK
                    addTask(context, taskbackup)
                }
            snackbar.show()
        } else if (action == UNDOACTION) {
            taskList.add(taskswift)
            notifyItemInserted(taskList.lastIndex)
            taskswift.status = ACTIVETASK
            editTask(context, taskswift)
        }
    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val CHECKACTION = "check"
        const val DELETEACTION = "delete"
        const val UNDOACTION = "undo"
        const val TAG = "Rv_TaskAdapter"
    }
}




package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.TaskModel
import com.bridesandgrooms.event.Model.UserModel
//import kotlinx.android.synthetic.main.task_item_layout2.view.*

class Rv_TaskAdapter2(val taskList: MutableList<Task>) :
    RecyclerView.Adapter<Rv_TaskAdapter2.ViewHolder>() {

    lateinit var context: Context
    var taskmodel = TaskModel()
    lateinit var taskdbhelper: TaskDBHelper
    lateinit var usermodel: UserModel

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.task_item_layout2, p0, false)
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
        p0.taskname?.setTextColor(
            when (taskList[p1].status) {
                "C" -> getCategory(taskList[p1].category).colorinactiveforeground.toColorInt()
                else -> getCategory(taskList[p1].category).colorforeground.toColorInt()
            }
        )
        p0.taskcategory?.text = taskList[p1].category
        p0.taskcategory?.setTextColor(
            when (taskList[p1].status) {
                "C" -> getCategory(taskList[p1].category).colorinactiveforeground.toColorInt()
                else -> getCategory(taskList[p1].category).colorforeground.toColorInt()
            }
        )
        //p0.itemView.taskcard.setCardBackgroundColor(getCategory(taskList[p1].category).colorbackground.toColorInt())
        p0.itemView.findViewById<CardView>(R.id.taskcard).setCardBackgroundColor(
        //p0.itemView.taskcard.setCardBackgroundColor(
            when (taskList[p1].status) {
                "C" -> getCategory(taskList[p1].category).colorinactivebackground.toColorInt()
                else -> getCategory(taskList[p1].category).colorbackground.toColorInt()
            }
        )
        //p0.taskstatus?.setTextColor(getCategory(taskList[p1].category).colorforeground.toColorInt())
        p0.taskstatus?.setTextColor(
            when (taskList[p1].status) {
                "C" -> getCategory(taskList[p1].category).colorinactiveforeground.toColorInt()
                else -> getCategory(taskList[p1].category).colorforeground.toColorInt()
            }
        )
        p0.taskstatus?.text = when (taskList[p1].status) {
            "A" -> context.getString(R.string.todo)
            "C" -> context.getString(R.string.done)
            else -> "unknown"
        }


//        p0.taskdate?.text = taskList[p1].date
//        p0.taskbudget?.text = taskList[p1].budget
//        val resourceId = context.resources.getIdentifier(getCategory(taskList[p1].category).drawable, "drawable",
//            context.packageName)
//        p0.categoryavatar?.setImageResource(resourceId)

        p0.itemView.setOnClickListener {
//            if(taskList[p1].status == "A") {
            val taskdetail = Intent(context, TaskCreateEdit::class.java)
            taskdetail.putExtra("task", taskList[p1])
            context.startActivity(taskdetail)
//            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById(R.id.taskname)
        val taskcategory: TextView? = itemView.findViewById(R.id.taskcategory)
        val taskstatus: TextView? = itemView.findViewById(R.id.taskstatus)
//        val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudgets)
//        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

//    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
//        if (action == CHECKACTION) {
//            val taskswift = taskList[position]
//            taskList.removeAt(position)
//            notifyItemRemoved(position)
//            taskswift.status = COMPLETETASK
//            editTask(context, taskswift)
//
//            val snackbar = Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    editTask(context, taskswift)
//                }
//            snackbar.show()
//        }
//    }
//
//    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
//        //val user = com.example.newevent2.Functions.getUserSession(context!!)
//        val taskswift = taskList[position]
//        val taskbackup = Task().apply {
//            name = taskswift.name
//            budget = taskswift.budget
//            date = taskswift.date
//            category = taskswift.category
//            status = taskswift.status
//            createdatetime = taskswift.createdatetime
//        }
//
//        if (action == DELETEACTION) {
//            taskList.removeAt(position)
//            notifyItemRemoved(position)
//            deleteTask(context, taskswift)
//
//            val snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    addTask(context, taskbackup)
//                }
//            snackbar.show()
//        } else if (action == UNDOACTION) {
//            taskList.add(taskswift)
//            notifyItemInserted(taskList.lastIndex)
//            taskswift.status = ACTIVETASK
//            editTask(context, taskswift)
//        }
//    }

    companion object {
        const val TAG = "Rv_TaskAdapter"
    }
}





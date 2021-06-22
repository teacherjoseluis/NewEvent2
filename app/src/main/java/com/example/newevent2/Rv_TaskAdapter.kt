package com.example.newevent2

import Application.Cache
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskModel
import com.google.android.material.snackbar.Snackbar

class Rv_TaskAdapter(val userid: String, val eventid: String, val taskList: MutableList<Task>) :
    RecyclerView.Adapter<Rv_TaskAdapter.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
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
        p0.taskdate?.text = taskList[p1].date
        p0.taskbudget?.text = taskList[p1].budget

        p0.itemView.setOnClickListener {
            val taskdetail = Intent(context, TaskCreateEdit::class.java)
            taskdetail.putExtra("task", taskList[p1])
            taskdetail.putExtra("userid", userid)
            taskdetail.putExtra("eventid", eventid)
            context.startActivity(taskdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById<TextView>(R.id.taskname)
        val taskdate: TextView? = itemView.findViewById<TextView>(R.id.taskdate)
        val taskbudget: TextView? = itemView.findViewById<TextView>(R.id.taskbudget)
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        val taskswift = taskList[position]

        taskList.removeAt(position)
        notifyItemRemoved(position)

        if (action == CHECKACTION) {
            val taskmodel = TaskModel()
            taskswift.status = COMPLETETASK
            taskmodel.editTask(
                userid,
                eventid,
                taskswift,
                object : TaskModel.FirebaseAddEditTaskSuccess {
                    override fun onTaskAddedEdited(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Task from cache
                            Log.i(TAG, "Task ${taskswift.key} was marked as Completed")
                            Cache.deletefromStorage(TaskCreateEdit.TASKENTITY, context)
                        }
                    }
                })

            Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    taskList.add(taskswift)
                    notifyItemInserted(taskList.lastIndex)
                    taskswift.status = ACTIVETASK
                    taskmodel.editTask(
                        userid,
                        eventid,
                        taskswift,
                        object : TaskModel.FirebaseAddEditTaskSuccess {
                            override fun onTaskAddedEdited(flag: Boolean) {
                                if (flag) {
                                    //Deleting all instances of Task from cache
                                    Log.i(
                                        TAG,
                                        "Undo action and Task ${taskswift.key} was marked back as Active"
                                    )
                                    Cache.deletefromStorage(TaskCreateEdit.TASKENTITY, context)
                                }
                            }
                        })
                }.show()
        }
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val taskswift = taskList[position]
        val taskbackup = Task().apply {
            name = taskswift.name
            budget = taskswift.budget
            date = taskswift.date
            category = taskswift.category
            status = taskswift.status
            createdatetime = taskswift.createdatetime
        }
        taskList.removeAt(position)
        notifyItemRemoved(position)

        val usersession =
            context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val tasksactive = usersession.getInt("tasksactive", 0)
        val sessionEditor = usersession!!.edit()

        if (action == DELETEACTION) {
            val taskmodel = TaskModel()
            taskmodel.deleteTask(
                userid,
                eventid,
                taskswift,
                tasksactive,
                object : TaskModel.FirebaseDeleteTaskSuccess {
                    override fun onTaskDeleted(flag: Boolean) {
                        if (flag) {
                            //Deleting all instances of Task from cache
                            Log.i(TAG, "Task ${taskswift.key} was Deleted")
                            sessionEditor.putInt("tasksactive", tasksactive - 1)
                            sessionEditor.apply()
                            Log.d(TAG, "User has currently ${tasksactive - 1} active tasks")
                            Cache.deletefromStorage(TaskCreateEdit.TASKENTITY, context)
                        }
                    }
                })


            Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    taskList.add(taskbackup)
                    notifyItemInserted(taskList.lastIndex)
                    taskmodel.addTask(
                        userid,
                        eventid,
                        taskbackup,
                        tasksactive,
                        object : TaskModel.FirebaseAddEditTaskSuccess {
                            override fun onTaskAddedEdited(flag: Boolean) {
                                if (flag) {
                                    Log.i(
                                        TAG,
                                        "Undo action and Task ${taskswift.key} was added back"
                                    )
                                    sessionEditor.putInt("tasksactive", tasksactive + 1)
                                    sessionEditor.apply()
                                    Log.d(TAG, "User has currently ${tasksactive + 1} active tasks")
                                    //Deleting all instances of Task from cache
                                    Cache.deletefromStorage(TASKENTITY, context)
                                }
                            }
                        })
                }.show()
        }
//        else if (action == UNDOACTION) {
//            taskswift.status = ACTIVETASK
//            val taskmodel = TaskModel()
//            taskmodel.editTask(
//                userid,
//                eventid,
//                taskswift,
//                object : TaskModel.FirebaseAddEditTaskSuccess {
//                    override fun onTaskAddedEdited(flag: Boolean) {
//                        if (flag) {
//                            //Deleting all instances of Task from cache
//                            Cache.deletefromStorage(TaskCreateEdit.TASKENTITY, context)
//                        }
//                    }
//                })
//        }
    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val CHECKACTION = "check"
        const val DELETEACTION = "delete"
        const val UNDOACTION = "undo"
        const val TAG = "Rv_TaskAdapter"
        const val TASKENTITY = "Task"
    }
}




package com.bridesandgrooms.event.UI.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Fragments.GuestFragmentActionListener
import com.bridesandgrooms.event.UI.Fragments.TaskFragmentActionListener

class TaskCalendarAdapter(
    private val fragmentActionListener: TaskFragmentActionListener,
    private val taskList: ArrayList<Task>,
    val context: Context
) : RecyclerView.Adapter<TaskCalendarAdapter.TaskViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_cardview_expanded, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    /**
     * This particular ViewHolder can handle a contracted and an expanded state. The expanded state can display more information about the Guest such as the RSVP and companions
     */
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCardView: ConstraintLayout = itemView.findViewById(R.id.taskcardview)
        private val taskImageView: ImageView = itemView.findViewById(R.id.taskImageView)
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskName)
        private val taskCategoryTextView: TextView = itemView.findViewById(R.id.taskCategory)
        private val taskDateTextView: TextView = itemView.findViewById(R.id.taskDate)
        private val taskBudgetTextView: TextView = itemView.findViewById(R.id.taskBudget)
        private val taskStatusTextView: TextView = itemView.findViewById(R.id.taskStatus)

        init {
            taskCardView.setOnClickListener {
                handleClick()
            }
        }

        fun bind(task: Task) {
            val resourceId = context.resources.getIdentifier(
                getCategory(task.category).drawable, "drawable",
                context.packageName
            )

            taskImageView.setImageResource(resourceId)
            taskNameTextView.text = task.name
            taskCategoryTextView.text = task.category
            taskDateTextView.text = context.getString(R.string.date_text, task.date ?: "")
            taskBudgetTextView.text = context.getString(R.string.budget_text, task.budget ?: "")
            taskStatusTextView.text = context.getString(R.string.status_text, task.status ?: "")
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val task = taskList[position]
                fragmentActionListener.onTaskFragmentWithData(task)
            }
        }
    }
}


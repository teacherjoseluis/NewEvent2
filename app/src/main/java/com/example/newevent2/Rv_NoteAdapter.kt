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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.new_note.*

// Recyclerview - Displays a scrolling list of elements based on large datasets
// The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
// The adapter creates view holders as needed. The adapter also binds the view holders to their data.
// It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.


class Rv_NoteAdapter(val noteList: MutableList<Note>) :
    RecyclerView.Adapter<Rv_NoteAdapter.ViewHolder>(), ItemTouchAdapterAction {
    // ViewGroup - Views container

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.note_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.notetitle?.text = noteList[p1].title
        p0.notedatetime?.text = noteList[p1].datetime
        p0.notesummary?.text = noteList[p1].summary
//        p0.notedatetime?.text = noteList[p1].key
//        p0.notedatetime?.text = noteList[p1].eventid


        p0.itemView.setOnClickListener {
            val notedetail = Intent(context, NewNote::class.java)
            notedetail.putExtra("notekey", noteList[p1].key)
            notedetail.putExtra("notetitle", noteList[p1].title)
            notedetail.putExtra("noteurl", noteList[p1].noteurl)
            notedetail.putExtra("eventid", noteList[p1].eventid)
//            taskdetail.putExtra("eventid", taskList[p1].eventid)
//            taskdetail.putExtra("name", taskList[p1].name)
//            taskdetail.putExtra("date", taskList[p1].date)
//            taskdetail.putExtra("category", taskList[p1].category)
//            taskdetail.putExtra("budget", taskList[p1].budget)
//            taskdetail.putExtra("status", taskList[p1].status)
//
            context.startActivity(notedetail)
        }
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notetitle: TextView? = itemView.findViewById<TextView>(R.id.notetitle)
        val notedatetime: TextView? = itemView.findViewById<TextView>(R.id.notedatetime)
        val notesummary: TextView? = itemView.findViewById<TextView>(R.id.notesummary)
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
//        val task = TaskEntity()
//        task.key = taskList[position].key
//        task.eventid = taskList[position].eventid
//        task.name = taskList[position].name
//        task.date = taskList[position].date
//        task.budget = taskList[position].budget
//
//        taskList.removeAt(position)
//        notifyItemRemoved(position)
//
//        if (action == "check") {
//            task.editTask("complete")
//
//            Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(task)
//                    notifyItemInserted(taskList.lastIndex)
//                    task.editTask("active")
//                }.show()
//        }
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val note = NoteEntity().apply {
            key = noteList[position].key
            title = noteList[position].title
            datetime = noteList[position].datetime
            noteurl = noteList[position].noteurl
            eventid = noteList[position].eventid
            summary = noteList[position].summary
        }

        noteList.removeAt(position)
        notifyItemRemoved(position)

        if (action == "delete") {
            note.deleteNote()
        }

//        val task = TaskEntity()
//        task.key = taskList[position].key
//        task.eventid = taskList[position].eventid
//        task.name = taskList[position].name
//        task.date = taskList[position].date
//        task.budget = taskList[position].budget
//        task.category = taskList[position].category
//
//        taskList.removeAt(position)
//        notifyItemRemoved(position)
//
//        if (action == "delete") {
//            task.deleteTask()
//
//            Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(task)
//                    notifyItemInserted(taskList.lastIndex)
//                    task.addTask()
//                }.show()
//        } else if (action == "undo") {
//            task.editTask("active")
//        }
    }
}




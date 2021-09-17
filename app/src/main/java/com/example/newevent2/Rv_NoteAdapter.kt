package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.Note
import com.example.newevent2.Model.NoteDBHelper
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

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
        p0.notedatetime?.text = noteList[p1].lastupdateddatetime
        p0.notesummary?.text = noteList[p1].title

        try {
            p0.itemView.background.setTint(noteList[p1].color.toColorInt())
        } catch (e: Exception) {
            p0.itemView.background.setTint(Color.WHITE)
        }
        p0.itemView.setOnClickListener {
            val notedetail = Intent(context, NoteCreateEdit::class.java)
            notedetail.putExtra("note", noteList[p1])
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

    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val noteswift = noteList[position]
        val notebackup = Note().apply {
            title = noteList[position].title
            body = noteList[position].body
            color = noteList[position].color
            lastupdateddatetime = noteList[position].lastupdateddatetime
        }

        val notedb = NoteDBHelper(context)
        if (action == DELETEACTION) {
            noteList.removeAt(position)
            notifyItemRemoved(position)
            notedb.delete(noteswift)

            val snackbar = Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    val notedbins = NoteDBHelper(context)
//                    noteList.add(notebackup)
//                    notifyItemInserted(noteList.lastIndex)
//                    notedbins.insert(notebackup)
//        }
            snackbar.show()
        }
    }

    companion object {
        const val DELETEACTION = "delete"
        const val TAG = "Rv_NoteAdapter"
    }
}




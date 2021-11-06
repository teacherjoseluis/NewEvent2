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
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.Note
import com.example.newevent2.Model.NoteDBHelper
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class Rv_NoteAdapter(private val noteList: MutableList<Note>) :
    RecyclerView.Adapter<Rv_NoteAdapter.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0.context).inflate(R.layout.note_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notetitle: TextView? = itemView.findViewById(R.id.notetitle)
        val notedatetime: TextView? = itemView.findViewById(R.id.notedatetime)
        val notesummary: TextView? = itemView.findViewById(R.id.notesummary)
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {

    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val noteswift = noteList[position]
        Note().apply {
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
            snackbar.show()
        }
    }

    companion object {
        const val DELETEACTION = "delete"
        const val TAG = "Rv_NoteAdapter"
    }
}




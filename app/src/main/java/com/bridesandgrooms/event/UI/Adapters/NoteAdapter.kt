package com.bridesandgrooms.event.UI.Adapters


import Application.AnalyticsManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.UI.Fragments.NoteFragmentActionListener
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Activities.ExportPDF.Companion.SCREEN_NAME

class NoteAdapter(
    private val fragmentActionListener: NoteFragmentActionListener,
    private val noteList: ArrayList<Note>,
    val context: Context
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout, parent, false)
        return NoteViewHolder(v)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.bind(note)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customNoteCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val transitionDuration = 300L

        init {
            customNoteCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "customNoteCardView", "click")
                handleClick()
            }
        }

        fun bind(note: Note) {
            customNoteCardView.removeAllViews()

            val layoutId = R.layout.note_item_layout
            val view =
                LayoutInflater.from(itemView.context).inflate(layoutId, customNoteCardView, false)

            view.findViewById<TextView>(R.id.notetitle).text = note.title
            view.findViewById<TextView>(R.id.notesummary).text = note.body
            view.findViewById<TextView>(R.id.notedatetime).text = note.lastupdateddatetime

            customNoteCardView.addView(view)
            animateView(view)
        }

        private fun animateView(view: View) {
            view.alpha = 0f // Set initial alpha to 0
            view.animate()
                .alpha(1f) // Animate alpha to 1
                .setDuration(transitionDuration)
                .start()
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val note = noteList[position]
                fragmentActionListener.onNoteFragmentWithData(note)
            }
        }
    }
}




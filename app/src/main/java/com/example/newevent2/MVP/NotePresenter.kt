package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.Model.Note
import com.example.newevent2.Model.NoteDBHelper
import com.example.newevent2.MyNotes

class NotePresenter(
    var context: Context,
    var fragment: MyNotes,
    val view: View
) {

    init {
        val notedb = NoteDBHelper(context)
        val notearray = notedb.getAllNotes()

        if (notearray.size == 0){
            fragment.onNoteError(view, ERRCODENOTE)
        } else {
            fragment.onNoteSuccess(view, notearray)
        }
    }

    interface NoteActivity {
        fun onNoteSuccess(inflatedView: View, notelist: ArrayList<Note>)
        fun onNoteError(inflatedView: View, errcode: String)
    }

    companion object {
        const val ERRCODENOTE = "NONOTES"
    }
}
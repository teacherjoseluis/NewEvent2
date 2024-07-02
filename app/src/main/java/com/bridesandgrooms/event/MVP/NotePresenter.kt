package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.Model.NoteDBHelper
import com.bridesandgrooms.event.UI.Fragments.MyNotes

class NotePresenter(
    var context: Context,
    var fragment: MyNotes
) {

    private var noteArray: ArrayList<Note>

    init {
        val notedb = NoteDBHelper(context)
        noteArray = notedb.getAllNotes()!!
    }

    fun getAllNotes() {
        if (noteArray.size == 0) {
            fragment.onNoteError(ERRCODENOTE)
        } else {
            fragment.onNoteSuccess(noteArray)
        }
    }

    interface NoteActivity {
        fun onNoteSuccess(noteList: ArrayList<Note>)
        fun onNoteError(errcode: String)
    }

    companion object {
        const val ERRCODENOTE = "NONOTES"
    }
}
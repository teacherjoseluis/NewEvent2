package com.bridesandgrooms.event.MVP

import android.content.Context
import android.view.View
import com.bridesandgrooms.event.Model.Note
import com.bridesandgrooms.event.Model.NoteDBHelper
import com.bridesandgrooms.event.MyNotes

class NotePresenter(
    var context: Context,
    var fragment: MyNotes,
    val view: View
) {

    init {
        val notedb = NoteDBHelper(context)
        val notearray = notedb.getAllNotes()!!

        if (notearray.size == 0){
            fragment.onNoteError(view, ERRCODENOTE)
        } else {
            fragment.onNoteSuccess(view, notearray)
        }
    }

    interface NoteActivity {
        fun onNoteSuccess(inflatedView: View, noteList: MutableList<Note>)
        fun onNoteError(inflatedView: View, errcode: String)
    }

    companion object {
        const val ERRCODENOTE = "NONOTES"
    }
}
package com.example.newevent2

import com.example.newevent2.Model.Note

interface FirebaseSuccessListenerNote {
    fun onNotesList(list: ArrayList<Note>)
}
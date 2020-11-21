package com.example.newevent2

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.collections.ArrayList

class NoteEntity : Note() {


    private val database = FirebaseDatabase.getInstance()
    private val storage = Firebase.storage

    private val myRef = database.reference
    private val storageRef = storage.reference

    fun getNotesList(dataFetched: FirebaseSuccessListenerNote) {
        val postRef = myRef.child("User").child("Event").child(this.eventid).child("Note")
        var notelist = ArrayList<Note>()

        val notelListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                notelist.clear()

                for (snapshot in p0.children) {
                    val noteitem = snapshot.getValue(Note::class.java)
                    noteitem!!.key = snapshot.key.toString()
                    notelist.add(noteitem!!)
                }
                dataFetched.onNotesList(notelist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(notelListenerActive)
    }

//        fun editNote() {
//            val postRef =
//                myRef.child("User").child("Event").child(this.eventid).child("Note").child(this.key)
//
//            val notes = hashMapOf(
//                "name" to name,
//                "budget" to budget,
//                "date" to date,
//                "category" to category,
//                "eventid" to eventid,
//                "status" to status
//            )
//
//            postRef.setValue(notes as Map<String, Any>)
//                .addOnFailureListener {
//                    return@addOnFailureListener
//                }
//                .addOnSuccessListener {
//                    return@addOnSuccessListener
//                }
//        }


    fun deleteNote() {
        myRef.child("User").child("Event").child(this.eventid).child("Note").child(this.key)
            .removeValue()
    }

    fun addNote(uri: Uri) {
        val postRef =
            myRef.child("User").child("Event").child(this.eventid).child("Note").push()

        val notes = hashMapOf(
            "title" to title,
            "datetime" to datetime,
            "noteurl" to noteurl,
            "eventid" to eventid,
            "summary" to summary
        )

        postRef.setValue(notes as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                if (uri != null) {
                    val notekey = postRef.key.toString()
                    val noteRef = storageRef.child("User/Event/$eventid/Note/$notekey/${uri.lastPathSegment}")
                    val uploadTask = noteRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        return@addOnFailureListener
                    }.addOnSuccessListener {
                        return@addOnSuccessListener
                    }
                }
            }
    }

    fun editNote(uri: Uri, oldurl:String) {
        val postRef =
            myRef.child("User").child("Event").child(this.eventid).child("Note").child(this.key)

        val notes = hashMapOf(
            "title" to title,
            "datetime" to datetime,
            "noteurl" to noteurl,
            "eventid" to eventid,
            "summary" to summary
        )

        postRef.setValue(notes as Map<String, Any>)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                if (uri != null) {
                    val notekey = postRef.key.toString()
                    val noteRef = storageRef.child("User/Event/$eventid/Note/$notekey/${uri.lastPathSegment}")
                    val uploadTask = noteRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        return@addOnFailureListener
                    }.addOnSuccessListener {
                        val oldfileRef = storageRef.child("User/Event/$eventid/Note/$notekey/$oldurl")
                        oldfileRef.delete()
                    }
                }
            }
    }
}

package com.example.newevent2

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.my_notes.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*

class MyNotes : AppCompatActivity() {

    var eventkey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_notes)
        val intent = intent

        eventkey = intent.getStringExtra("eventkey").toString()

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val recyclerView = recyclerViewNotes
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
//----------------------------------------------------------------------------------------------
        val notentity = NoteEntity()
        notentity.eventid = eventkey

        notentity.getNotesList(object : FirebaseSuccessListenerNote {
            override fun onNotesList(list: ArrayList<Note>) {
                val rvAdapter = Rv_NoteAdapter(list)
                recyclerView.adapter = rvAdapter

//                val swipeController =
//                    SwipeControllerTasks(inf.context, rvAdapter, recyclerViewComplete, null, "undo")
//                val itemTouchHelper = ItemTouchHelper(swipeController)
//                itemTouchHelper.attachToRecyclerView(recyclerViewComplete)
            }
        })
//----------------------------------------------------------------------------------------------

        floatingNewNote.setOnClickListener {
            val newnote = Intent(this, NewNote::class.java)
            newnote.putExtra("eventkey", eventkey)
            startActivity(newnote)
        }

    }
}


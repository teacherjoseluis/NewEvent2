package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_events.*
import kotlinx.android.synthetic.main.navbottom.*
import javax.xml.transform.Source


class MyEvents : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_events)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Events"


//        initialize the recyclerView from the XML
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEvent)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyEvents).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        // Query DB and populate RecyclerView
        val evententity = EventEntity()

        evententity.getEvents(object : FirebaseSuccessListenerEvent {
            override fun onEventList(list: ArrayList<Event>) {
                val rvEventAdapter = RvEventAdapter(list)
                recyclerView.adapter = rvEventAdapter
            }
        })

        NewEventActionButton.setOnClickListener()
        {
            val newevent = Intent(this, MainActivity::class.java)
            startActivity(newevent)
        }

    }
}


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
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        Initializing the type of layout, here I have used LinearLayoutManager you can try GridLayoutManager
//        Based on your requirement to allow vertical or horizontal scroll , you can change it in  LinearLayout.VERTICAL

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyEvents).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }


//        Create an arraylist

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        val postRef = myRef.child("User").child("Event")
        var eventlist = ArrayList<Event>()

        val eventListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                eventlist.clear()
                if (p0.exists()) {

                    for (snapshot in p0.children) {
                        val eventitem = snapshot.getValue(Event::class.java)
                        eventitem!!.key = snapshot.key.toString()
                        eventlist.add(eventitem!!)
                    }
                }

                //        pass the values to RvAdapter
                val rvAdapter = RvAdapter(eventlist)
//        set the recyclerView to the adapter
                recyclerView.adapter = rvAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListener)

        floatingActionButton.setOnClickListener()
        {
            val newevent = Intent(this, MainActivity::class.java)
            startActivity(newevent)
        }


//        floatingActionButton2.setOnClickListener {
//            val contacts = Intent(this, MyContacts::class.java)
//            startActivity(contacts)
//        }

        imageButton3.setOnClickListener {
            val contacts = Intent(this, MyContacts::class.java)
            startActivity(contacts)
        }

    }
}


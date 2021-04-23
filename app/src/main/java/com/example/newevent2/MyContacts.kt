package com.example.newevent2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.navbottom.*

class MyContacts : AppCompatActivity() {

    var eventkey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts)
        val intent = intent

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

        // Toolbar
//        setSupportActionBar(findViewById(R.id.toolbar))
//        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //------------------------------------------------------------------------------------
        //val eventspinner = findViewById<Spinner>(R.id.eventspinner)
//        val evententity = EventEntity()
//        evententity.getEventNames(object : FirebaseSuccessListenerList {
//            override fun onListCreated(list: ArrayList<Any>) {
//                val eventlistadapter =
//                    ArrayAdapter(this@MyContacts, R.layout.simple_spinner_item_event, list)
//                eventlistadapter!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_event)
//                eventspinner.adapter = null
//                eventspinner.adapter = eventlistadapter
//                Log.i("SpinnerList", list.toString())
//            }
//        })
        //------------------------------------------------------------------------------------

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Contacts"

//56 to 116 tab logic
        if (viewPager != null) {
            val adapter =
                Contacts_PagerAdapter(
                    applicationContext,
                    supportFragmentManager,
                    tablayout.tabCount
                )
            viewPager.adapter = adapter

            viewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tablayout
                )
            )

            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position

                    if (p0.position == 0) {

                        findViewById<FloatingActionButton>(R.id.floatingActionButtonGuest).isVisible =
                            false
                        findViewById<FloatingActionButton>(R.id.floatingActionButtonVendor).isVisible =
                            false
                        //findViewById<TextView>(R.id.eventlabel).isVisible = true
                        //findViewById<Spinner>(R.id.eventspinner).isVisible = true

                        findViewById<ConstraintLayout>(R.id.LocalLayout).isVisible = false
                        findViewById<ConstraintLayout>(R.id.GoogleLayout).isVisible = false

                    } else if (p0.position == 1) {
                        findViewById<FloatingActionButton>(R.id.floatingActionButtonGuest).isVisible =
                            true
                        findViewById<FloatingActionButton>(R.id.floatingActionButtonVendor).isVisible =
                            false
                        //findViewById<TextView>(R.id.eventlabel).isVisible = true
                        //findViewById<Spinner>(R.id.eventspinner).isVisible = true

                        findViewById<ConstraintLayout>(R.id.LocalLayout).isVisible = false
                        findViewById<ConstraintLayout>(R.id.GoogleLayout).isVisible = false

                    } else if (p0.position == 2) {
                        findViewById<FloatingActionButton>(R.id.floatingActionButtonGuest).isVisible =
                            false
                        findViewById<FloatingActionButton>(R.id.floatingActionButtonVendor).isVisible =
                            true
                        //findViewById<TextView>(R.id.eventlabel).isVisible = false
                        //findViewById<Spinner>(R.id.eventspinner).isVisible = false
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }

        imageButton1.setOnClickListener {
            val home = Intent(this, Welcome::class.java)
            startActivity(home)
        }
        imageButton2.setOnClickListener {
            val calendar = Intent(this, MyCalendar::class.java)
            //calendar.putExtra("eventkey", eventkey)
            startActivity(calendar)
        }

        imageButton.setOnClickListener {
            val events = Intent(this, EventDetail::class.java)
            //contacts.putExtra("eventkey", eventkey)
            startActivity(events)
        }

        imageButton4.setOnClickListener {
            val notes = Intent(this, MyNotes::class.java)
            //notes.putExtra("eventkey", eventkey)
            startActivity(notes)
        }
    }
}


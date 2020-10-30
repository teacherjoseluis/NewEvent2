package com.example.newevent2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MyContacts : AppCompatActivity() {

    var eventkey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts)
        val intent = intent

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

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
                        val actionbutton =
                            findViewById<FloatingActionButton>(R.id.floatingActionButton)
                        actionbutton.isVisible = false
                    } else if (p0.position == 1) {
                        val actionbutton =
                            findViewById<FloatingActionButton>(R.id.floatingActionButton)
                        actionbutton.isVisible = true
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Contacts"

    }

//    override fun onComplete() {
//        val eventspinner = findViewById<Spinner>(R.id.eventspinner)
//        val evententity = EventEntity()
//        evententity.getEventNames(object : FirebaseSuccessListenerHashMap {
//
//            override fun onHashMapCreated(map: HashMap<String, String>) {
//                val eventlist = ArrayList<String>(map.values)
//                val eventlistadapter =
//                    ArrayAdapter(
//                        this@MyContacts,
//                        R.layout.simple_spinner_item_event,
//                        eventlist
//                    )
//
//                eventlistadapter!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_event)
//                eventspinner.adapter = null
//                eventspinner.adapter = eventlistadapter
//                Log.i(
//                    "SPINNER",
//                    "************ Estoy agregando al spinner **************************"
//                )
//                Log.i("SpinnerList", eventlist.toString())
//
//                eventspinner.onItemSelectedListener =
//                    object : AdapterView.OnItemSelectedListener {
//                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            TODO("Not yet implemented")
//                        }
//
//                        override fun onItemSelected(
//                            p0: AdapterView<*>?,
//                            p1: View?,
//                            p2: Int,
//                            p3: Long
//                        ) {
//                            val eventname = p0!!.getItemAtPosition(p2).toString()
//                            eventkey =
//                                map.filterValues { it == eventname }.keys.toString()
//                        }
//                    }
//
//            }
//        })
//    }


}


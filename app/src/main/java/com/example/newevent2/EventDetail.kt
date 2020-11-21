package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.navbottom.*

class EventDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_detail)
        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        // Toolbar and Title
        //---------------------------------------------------------------------
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Event Detail"
        //---------------------------------------------------------------------
        val intent = intent
        val eventkey = intent.getStringExtra("eventkey").toString()
        val imageurl = intent.getStringExtra("imageurl").toString()
        val name = intent.getStringExtra("name").toString()
        val placeid = intent.getStringExtra("placeid").toString()
        val date = intent.getStringExtra("date").toString()
        val time = intent.getStringExtra("time").toString()
        val about = intent.getStringExtra("about").toString()
        val location = intent.getStringExtra("location").toString()
        val address = intent.getStringExtra("address").toString()
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        if (viewPager != null) {
            val adapter = EventPagerAdapter(
//                this,
                supportFragmentManager,
                tablayout.tabCount,
                eventkey,
                imageurl,
                name,
                placeid,
                date,
                time,
                about,
                location,
                address,
                latitude,
                longitude
            )
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position

                    if (p0.position == 0) {
                        findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
                            true
                    } else if (p0.position == 1) {
                        findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
                            false

                        findViewById<ConstraintLayout>(R.id.TaskLayout).isVisible = false
                        findViewById<ConstraintLayout>(R.id.PaymentLayout).isVisible = false
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }

        imageButton2.setOnClickListener {
            val calendar = Intent(this, MyCalendar::class.java)
            calendar.putExtra("eventkey", eventkey)
            startActivity(calendar)
        }

        imageButton3.setOnClickListener {
            val contacts = Intent(this, MyContacts::class.java)
            contacts.putExtra("eventkey", eventkey)
            startActivity(contacts)
        }

        imageButton4.setOnClickListener {
            val notes = Intent(this, MyNotes::class.java)
            notes.putExtra("eventkey", eventkey)
            startActivity(notes)
        }

    }

}
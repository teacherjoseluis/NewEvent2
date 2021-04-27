package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Model.User
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.navbottom.*

class MainEventView : AppCompatActivity() {

    val userid = ""
    val eventid = ""
    var usersession = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_detail)

        usersession = com.example.newevent2.Functions.getUserSession(this)
        if (usersession.key == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
        }

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Event Detail"

        if (viewPager != null) {
            val adapter = MainEventPagerAdapter(
                supportFragmentManager,
                usersession.key,
                usersession.eventid,
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
//                                findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                                    true
                    } else if (p0.position == 1) {
//                                findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                                    false
//
//                                findViewById<ConstraintLayout>(R.id.TaskLayout).isVisible = false
//                                findViewById<ConstraintLayout>(R.id.PaymentLayout).isVisible = false
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                }
            })
        }
//            }
//
//        })
        //---------------------------------------------------------------------

//        val event = EventEntity()
//        event.getEventdetail(this, object : FirebaseSuccessListenerEvent {
//            override fun onEventList(list: ArrayList<Event>) {
//                TODO("Not yet implemented")
//            }

//            override fun onEvent(event: Event) {
//                if (viewPager != null) {
//                    val adapter = EventPagerAdapter(
//                        supportFragmentManager,
//                        tablayout.tabCount,
//                        event.key,
//                        event.imageurl,
//                        event.name,
//                        event.placeid,
//                        event.date,
//                        event.time,
//                        event.about,
//                        event.location,
//                        event.address,
//                        event.latitude,
//                        event.longitude
//                    )
//                    viewPager.adapter = adapter
//                    viewPager.addOnPageChangeListener(
//                        TabLayout.TabLayoutOnPageChangeListener(
//                            tablayout
//                        )
//                    )
//
//                    tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                        override fun onTabSelected(p0: TabLayout.Tab?) {
//                            viewPager.currentItem = p0!!.position
//
//                            if (p0.position == 0) {
//                                findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                                    true
//                            } else if (p0.position == 1) {
//                                findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                                    false
//
//                                findViewById<ConstraintLayout>(R.id.TaskLayout).isVisible = false
//                                findViewById<ConstraintLayout>(R.id.PaymentLayout).isVisible = false
//                            }
//                        }
//
//                        override fun onTabUnselected(p0: TabLayout.Tab?) {
//
//                        }
//
//                        override fun onTabReselected(p0: TabLayout.Tab?) {
//
//                        }
//                    })
//                }
//            }
//
//        })

        imageButton1.setOnClickListener {
            val home = Intent(this, Welcome::class.java)
            startActivity(home)
        }
        imageButton2.setOnClickListener {
            val calendar = Intent(this, MyCalendar::class.java)
            //calendar.putExtra("eventkey", eventkey)
            startActivity(calendar)
        }

        imageButton3.setOnClickListener {
            val contacts = Intent(this, MyContacts::class.java)
            //contacts.putExtra("eventkey", eventkey)
            startActivity(contacts)
        }

        imageButton4.setOnClickListener {
            val notes = Intent(this, MyNotes::class.java)
            //notes.putExtra("eventkey", eventkey)
            startActivity(notes)
        }

    }

//    override fun onSupportNavigateUp(): Boolean {
//        finish()
//        return true
//    }

}

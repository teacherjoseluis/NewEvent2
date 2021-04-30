package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Model.User
import com.google.android.material.tabs.TabLayout

class TaskPaymentList : AppCompatActivity() {

    val userid = ""
    val eventid = ""
    var usersession = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskpayment_list)

        usersession = com.example.newevent2.Functions.getUserSession(this)
        if (usersession.key == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
        }

        val intent = intent
        val category = intent.getStringExtra("category").toString()

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (viewPager != null) {0
            val adapter = TaskPayment_PagerAdapter(
                this,
                supportFragmentManager,
                usersession.key,
                usersession.eventid,
                category,
                tablayout.tabCount
            )
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        when (category) {
            "venue" -> apptitle.text = "Venue"
            "photo" -> apptitle.text = "Photo & Video"
            "entertainment" -> apptitle.text = "Entertainment"
            "flowers" -> apptitle.text = "Flowers & Decor"
            "transportation" -> apptitle.text = "Transportation"
            "ceremony" -> apptitle.text = "Ceremony"
            "attire" -> apptitle.text = "Attire & Accessories"
            "beauty" -> apptitle.text = "Health & Beauty"
            "food" -> apptitle.text = "Food & Drink"
            "guests" -> apptitle.text = "Guests"
            else -> apptitle.text = "No Category"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
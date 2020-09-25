package com.example.newevent2

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskList : AppCompatActivity() {

    lateinit var eventkey:String
    lateinit var category:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list)
        val intent = intent
        eventkey = intent.getStringExtra("eventkey").toString()
        category=intent.getStringExtra("category").toString()

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        //val viewPager = findViewById<CustomViewPager>(R.id.pager)

        if (viewPager != null) {
            val adapter = TaskList_PagerAdapter(this, supportFragmentManager, tablayout.tabCount, eventkey, category)
            viewPager.adapter = adapter
            //viewPager.setSwipeable(false)
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

            tablayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        if (category == "venue")
            apptitle.text = "Venue"
        else if (category == "photo")
            apptitle.text = "Photo & Video"
        else if (category == "entertainment")
            apptitle.text = "Entertainment"
        else if (category == "flowers")
            apptitle.text = "Flowers & Decor"
        else
            apptitle.text = "No Category"

    }

}
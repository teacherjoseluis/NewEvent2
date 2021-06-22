//package com.example.newevent2
//
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil.setContentView
//import androidx.viewpager.widget.ViewPager
//import com.google.android.material.tabs.TabLayout
//
//class NewTask : AppCompatActivity() {
//
//    lateinit var eventkey:String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.new_task)
//
//        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
//        val viewPager = findViewById<View>(R.id.pager) as ViewPager
//
//        eventkey = intent.getStringExtra("eventkey").toString()
//
//        if (viewPager != null) {
//            //val adapter = NewTask_PagerAdapter(this, supportFragmentManager, tablayout.tabCount, eventkey)
//            //viewPager.adapter = adapter
//            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
//
//            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(p0: TabLayout.Tab?) {
//                    viewPager.currentItem = p0!!.position
//                }
//
//                override fun onTabUnselected(p0: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabReselected(p0: TabLayout.Tab?) {
//
//                }
//            })
//        }
//
//        // Title and Toolbar
//        //--------------------------------------------------------------------------------
//        setSupportActionBar(findViewById(R.id.toolbar))
//        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//        val apptitle = findViewById<TextView>(R.id.appbartitle)
//        apptitle.text = "Task"
//
//    }
//}
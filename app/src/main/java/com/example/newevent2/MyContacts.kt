package com.example.newevent2

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.contacts.*

class MyContacts : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts)
        val intent = intent

        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

        if (viewPager != null) {
            val adapter = Contacts_PagerAdapter(this, supportFragmentManager, tablayout.tabCount)
            viewPager.adapter = adapter

            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

            tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position

                    if (p0.position == 0){
                        val actionbutton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
                        actionbutton.isVisible = false
                    } else if (p0.position == 1){
                        val actionbutton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
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

}
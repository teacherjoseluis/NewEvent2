package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Model.User
import com.example.newevent2.ui.ViewAnimation
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.event_detail.view.*
import kotlinx.android.synthetic.main.navbottom.*

class MainEventView_clone() : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.event_detail, container, false)

        val tablayout = inf.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = inf.findViewById<View>(R.id.pager) as ViewPager
        val userSession=com.example.newevent2.Functions.getUserSession(activity!!.applicationContext)

//        val apptitle = findViewById<TextView>(R.id.appbartitle)
//        apptitle.text = "Event Detail"

        val adapter = MainEventPagerAdapter(
            userSession.key,
            userSession.eventid,
            userSession.language,
            childFragmentManager,
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
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })
        return inf
    }
}
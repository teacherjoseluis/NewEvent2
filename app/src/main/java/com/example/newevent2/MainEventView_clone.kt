package com.example.newevent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Functions.userdbhelper
import com.google.android.material.tabs.TabLayout

class MainEventView_clone() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.event_detail, container, false)

        //Declare tablayout and get user details from the SharePreferences
        val tablayout = inf.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = inf.findViewById<View>(R.id.pager) as ViewPager
        val user = userdbhelper.getUser(userdbhelper.getUserKey())

        // Declare adapter and pass key, eventid and language as parameters
        val adapter = MainEventPagerAdapter(
            user.key,
            user.eventid,
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
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
        return inf
    }
}
package com.example.newevent2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.Model.User
import com.google.android.material.tabs.TabLayout
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport

class MainEventView_clone : Fragment() {

    private lateinit var user:User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.event_detail, container, false)

        //Declare tablayout and get user details from the SharePreferences
        user = userdbhelper.getUser(userdbhelper.getUserKey())

//      Declare adapter and pass key, eventid and language as parameters
//        val adapter = MainEventPagerAdapter(
//            user.key,
//            user.eventid,
//            childFragmentManager,
//            tablayout.tabCount
//        )
//
//        viewPager.adapter = adapter
//        viewPager.addOnPageChangeListener(
//            TabLayout.TabLayoutOnPageChangeListener(
//                tablayout
//            )
//        )
//
//        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(p0: TabLayout.Tab?) {
//                viewPager.currentItem = p0!!.position
//            }
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {}
//            override fun onTabReselected(p0: TabLayout.Tab?) {}
//        })
        return inf
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MainEventPagerAdapter(
            user.userid!!,
            user.eventid,
            childFragmentManager
        )
        val viewPager = view.findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = adapter

        val tablayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tablayout.setupWithViewPager(viewPager)
    }
}


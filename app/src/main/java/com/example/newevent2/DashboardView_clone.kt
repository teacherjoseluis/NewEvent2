package com.example.newevent2

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.Model.UserDBHelper
import com.google.android.material.tabs.TabLayout

class DashboardView_clone() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.welcome0, container, false)

        val tablayout = inf.findViewById<TabLayout>(R.id.dashboard_tabLayout)
        val viewPager = inf.findViewById<View>(R.id.dashboardpager) as ViewPager

        //Getting the user currently loaded
        userdbhelper = UserDBHelper(activity!!.applicationContext)
        val userSession = userdbhelper.getUser(userdbhelper.getUserKey())

        //Setting up the pager adapter for this view
        val adapter =
            DashboardPagerAdapter(
                userSession.key!!,
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

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
        return inf
    }

    override fun onDestroyView() {
        val childFragments = childFragmentManager.fragments
        if (childFragments.isNotEmpty()) {
            val fragmentTransaction = childFragmentManager.beginTransaction()
            childFragments.forEach {
                if (it != null) {
                    fragmentTransaction.remove(it)
                }
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
        super.onDestroyView()
    }
}
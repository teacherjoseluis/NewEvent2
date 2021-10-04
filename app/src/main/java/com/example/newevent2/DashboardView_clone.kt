package com.example.newevent2

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Model.User
import com.google.android.material.tabs.TabLayout

class DashboardView_clone() : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.welcome0, container, false)

        val tablayout = inf.findViewById<TabLayout>(R.id.dashboard_tabLayout)
        val viewPager = inf.findViewById<View>(R.id.dashboardpager) as ViewPager
        val userSession=com.example.newevent2.Functions.getUserSession(activity!!.applicationContext)
        if (viewPager != null) {
            val adapter =
                DashboardPagerAdapter(
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
        }

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
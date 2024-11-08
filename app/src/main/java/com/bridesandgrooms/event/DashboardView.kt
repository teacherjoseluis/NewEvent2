package com.bridesandgrooms.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.UI.Adapters.DashboardPagerAdapter
import com.google.android.material.tabs.TabLayout

class DashboardView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.welcome0, container, false)
        val tablayout = inf.findViewById<TabLayout>(R.id.dashboard_tabLayout)
        val viewPager = inf.findViewById<View>(R.id.dashboardpager) as ViewPager

        val userSession = User().getUser(requireActivity())

        //Setting up the pager adapter for this view
        val adapter =
            DashboardPagerAdapter(
                userSession.userid!!,
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
                val enableforyoutab = RemoteConfigSingleton.get_enable_foryoutab()
                if ((p0!!.position != 0) && !enableforyoutab){
                    Toast.makeText(context, context!!.getString(R.string.comingsoon), Toast.LENGTH_LONG).show()
                } else {
                    viewPager.currentItem = p0!!.position
                }
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
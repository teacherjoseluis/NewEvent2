package com.example.newevent2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class Dashboard_PagerAdapter(
    private val view: DashboardView,
    private val userid: String,
    private val eventid: String,
    fm: FragmentManager,
    private var totalTabs: Int
) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val bundle = Bundle()
                val fragInfo = DashboardActivity(view)
                bundle.putString("userid", userid)
                bundle.putString("eventid", eventid)
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val bundle = Bundle()
                val fragInfo = DashboardEvent(view)
                bundle.putString("userid", userid)
                bundle.putString("eventid", eventid)
                fragInfo.arguments = bundle
                return fragInfo
            }
            2 -> {
                val bundle = Bundle()
                val fragInfo = DashboardBlog(view)
                //bundle.putString("eventkey", eventkey)
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val bundle = Bundle()
                val fragInfo = DashboardActivity(view)
                bundle.putString("userid", userid)
                bundle.putString("eventid", eventid)
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
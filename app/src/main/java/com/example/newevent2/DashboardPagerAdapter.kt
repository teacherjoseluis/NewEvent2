package com.example.newevent2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class DashboardPagerAdapter(
    private val userid: String,
    private val eventid: String,
    private val language: String,
    fm: FragmentManager?,
    private var totalTabs: Int
) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("userid", userid)
        bundle.putString("eventid", eventid)

        return when (position) {
            0 -> {
                val fragInfo = DashboardEvent()
                fragInfo.arguments = bundle
                return fragInfo
            }
//            1 -> {
//                val fragInfo = DashboardActivity()
//                fragInfo.arguments = bundle
//                return fragInfo
//            }
            1 -> {
                val fragInfo = DashboardBlog()
                bundle.putString("language", language)
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val fragInfo = DashboardEvent()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
package com.example.newevent2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainEventPagerAdapter(
    fm: FragmentManager,
    private val userid: String,
    private val eventid: String,
    private var totalTabs: Int
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("userid", userid)
        bundle.putString("eventid", eventid)

        return when (position) {
            0 -> {
                val fragInfo = MainEventSummary()
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val fragInfo = MainEventDetail()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val fragInfo = MainEventSummary()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
package com.example.newevent2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class Event_PagerAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    internal var totalTabs: Int,
    val eventkey: String,
    val imageurl: String

) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> return EventDetail_Summary()
            1 -> {
                val bundle = Bundle()
                bundle.putString("eventkey", eventkey)
                bundle.putString("imageurl", imageurl)
                val fragInfo = EventDetail_Event()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> EventDetail_Summary()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}
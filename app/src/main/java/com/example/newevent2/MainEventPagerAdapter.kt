package com.example.newevent2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainEventPagerAdapter(
    private val userid: String,
    private val eventid: String,
    fm: FragmentManager?,
    private var totalTabs: Int
) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("userid", userid)
        bundle.putString("eventid", eventid)

        // Depending on the selected tab, it goes to EventCategories (default) or Vendors
        return when (position) {
            0 -> {
                val fragInfo = EventCategories()
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val fragInfo = VendorsAll()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val fragInfo = EventCategories()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
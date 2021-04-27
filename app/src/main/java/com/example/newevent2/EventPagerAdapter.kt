package com.example.newevent2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class EventPagerAdapter(
    fm: FragmentManager,
    private var totalTabs: Int
//    ,
//    private val eventkey: String,
//    private val imageurl: String,
//    private val name: String,
//    private val placeid: String,
//    private val date: String,
//    private val time: String,
//    private val about: String,
//    private val location: String,
//    private val address: String,
//    private val latitude: Double,
//    private val longitude: Double

) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
//        bundle.putString("eventkey", eventkey)
//        bundle.putString("imageurl", imageurl)
//        bundle.putString("name", name)
//        bundle.putString("placeid", placeid)
//        bundle.putString("date", date)
//        bundle.putString("time", time)
//        bundle.putString("about", about)
//        bundle.putString("location", location)
//        bundle.putString("address", address)
//        bundle.putDouble("latitude", latitude)
//        bundle.putDouble("longitude", longitude)

        return when (position) {
            0 -> {
                val fragInfo = EventDetailSummary()
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val fragInfo = EventDetailEvent()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val fragInfo = EventDetailSummary()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}
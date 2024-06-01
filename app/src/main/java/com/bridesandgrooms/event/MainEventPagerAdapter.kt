package com.bridesandgrooms.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bridesandgrooms.event.UI.Fragments.VendorsAll

class MainEventPagerAdapter(
    private val userid: String,
    private val eventid: String,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var fragments: MutableList<Fragment> = object : ArrayList<Fragment>() {
        init {
            add(EventCategories())
            add(VendorsAll())
        }
    }

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("userid", userid)
        bundle.putString("eventid", eventid)

        // Depending on the selected tab, it goes to EventCategories (default) or Vendors
        val fragInfo = fragments[position]
        fragInfo.arguments = bundle
        return fragInfo
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
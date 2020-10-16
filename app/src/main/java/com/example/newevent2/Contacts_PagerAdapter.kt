package com.example.newevent2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class Contacts_PagerAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    internal var totalTabs: Int

    ) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                val bundle = Bundle()
                val fragInfo = ContactsAll()
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val bundle = Bundle()
                val fragInfo = GuestsAll()
                fragInfo.arguments = bundle
                return fragInfo
            }
            2 -> {
                val bundle = Bundle()
                val fragInfo = ContactsAll()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val bundle = Bundle()
                val fragInfo = ContactsAll()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
package com.example.newevent2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TaskPayment_PagerAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    internal var totalTabs: Int,
    //val eventkey: String,
    val category: String

) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        //bundle.putString("eventkey", eventkey)
        bundle.putString("category", category)

        return when (position) {
            0 -> {
                val fragInfo = TaskPayment_Tasks()
                fragInfo.arguments = bundle
                return fragInfo
            }
            1 -> {
                val fragInfo = TaskPayment_Payments()
                fragInfo.arguments = bundle
                return fragInfo
            }
            else -> {
                val fragInfo = TaskPayment_Tasks()
                fragInfo.arguments = bundle
                return fragInfo
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}
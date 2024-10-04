package com.bridesandgrooms.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.TaskModel
import com.bridesandgrooms.event.UI.Adapters.TaskPayment_PagerAdapter
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

class TaskPaymentList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.taskpayment_list, container, false)
        val passedcategory = arguments?.getString("category") ?: ""

        // Declaring and initializing the Toolbar
        val tablayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager>(R.id.pager)

        //Declaring the PageAdapter and invoking it
        val adapter = TaskPayment_PagerAdapter(
            childFragmentManager,
            passedcategory,
            TaskModel.ACTIVESTATUS,
            tablayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.currentItem = p0!!.position
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        // Every container page will have the title name for the associated category
        val apptitle = view.findViewById<TextView>(R.id.appbartitle)
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        val language = this.resources.configuration.locales.get(0).language

        for (category in list) {
            if (category.code == passedcategory) {
                apptitle.text = when (language) {
                    "en" -> category.en_name
                    else -> category.es_name
                }
            }
        }
        return view
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }
}
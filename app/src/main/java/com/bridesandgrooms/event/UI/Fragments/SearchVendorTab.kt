package com.bridesandgrooms.event.UI.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TaskPaymentPayments
import com.bridesandgrooms.event.TaskPaymentTasks
import com.bridesandgrooms.event.databinding.SearchVendorsTabsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout

class SearchVendorTab : Fragment() {

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.search_vendors_tabs, container, false)

        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text =
            getString(R.string.searchvendor_title)

        val tablayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = rootView.findViewById<ViewPager>(R.id.pager)

        //Declaring the PageAdapter and invoking it
        val adapter = SearchVendorPagerAdapter(
            context,
            parentFragmentManager,
            tablayout.tabCount
        )

        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    viewPager.currentItem = p0!!.position
                }
                override fun onTabUnselected(p0: TabLayout.Tab?) {}
                override fun onTabReselected(p0: TabLayout.Tab?) {}
            })
        return rootView
    }
}

class SearchVendorPagerAdapter(
    private var context: Context,
    fm: FragmentManager,
    private var totalTabs: Int
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()

        when (position) {
            0 -> return createFragmentWithQuery(R.string.query_category1, Category.Venue.code)
            1 -> return createFragmentWithQuery(R.string.query_category2, Category.Photo.code)
            2 -> return createFragmentWithQuery(R.string.query_category3, Category.Entertainment.code)
            3 -> return createFragmentWithQuery(R.string.query_category4, Category.Flowers.code)
            4 -> return createFragmentWithQuery(R.string.query_category5, Category.Transportation.code)
            5 -> return createFragmentWithQuery(R.string.query_category6, Category.Ceremony.code)
            6 -> return createFragmentWithQuery(R.string.query_category7, Category.Accesories.code)
            7 -> return createFragmentWithQuery(R.string.query_category8, Category.Beauty.code)
            8 -> return createFragmentWithQuery(R.string.query_category9, Category.Food.code)
            else -> return createFragmentWithQuery(R.string.query_category9, Category.Food.code)
        }
    }

    private fun createFragmentWithQuery(queryId: Int, category: String): Fragment {
        val bundle = Bundle()
        bundle.putString("query", context.getString(queryId))
        bundle.putString("category", category)
        val fragInfo = SearchVendorFragment()
        fragInfo.arguments = bundle
        return fragInfo
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
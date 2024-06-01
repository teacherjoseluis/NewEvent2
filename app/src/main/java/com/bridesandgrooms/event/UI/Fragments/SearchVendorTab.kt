package com.bridesandgrooms.event.UI.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.R
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
        val language = context.resources.configuration.locales.get(0).language
        when (position) {
            0 -> return createFragmentWithQuery(if(language.equals("es")) Category.Venue.searchQueryEs else Category.Venue.searchQueryEn, Category.Venue.code)
            1 -> return createFragmentWithQuery(if(language.equals("es")) Category.Photo.searchQueryEs else Category.Photo.searchQueryEn, Category.Photo.code)
            2 -> return createFragmentWithQuery(if(language.equals("es")) Category.Entertainment.searchQueryEs else Category.Entertainment.searchQueryEn, Category.Entertainment.code)
            3 -> return createFragmentWithQuery(if(language.equals("es")) Category.Flowers.searchQueryEs else Category.Flowers.searchQueryEn, Category.Flowers.code)
            4 -> return createFragmentWithQuery(if(language.equals("es")) Category.Transportation.searchQueryEs else Category.Transportation.searchQueryEn, Category.Transportation.code)
            5 -> return createFragmentWithQuery(if(language.equals("es")) Category.Ceremony.searchQueryEs else Category.Ceremony.searchQueryEn, Category.Ceremony.code)
            6 -> return createFragmentWithQuery(if(language.equals("es")) Category.Accesories.searchQueryEs else Category.Accesories.searchQueryEn, Category.Accesories.code)
            7 -> return createFragmentWithQuery(if(language.equals("es")) Category.Beauty.searchQueryEs else Category.Beauty.searchQueryEn, Category.Beauty.code)
            8 -> return createFragmentWithQuery(if(language.equals("es")) Category.Food.searchQueryEs else Category.Food.searchQueryEn, Category.Food.code)
            else -> return createFragmentWithQuery(if(language.equals("es")) Category.Food.searchQueryEs else Category.Food.searchQueryEn, Category.Food.code)
        }
    }

    private fun createFragmentWithQuery(query: String, category: String): Fragment {
        val bundle = Bundle()
        bundle.putString("query", query)
        bundle.putString("category", category)
        val fragInfo = SearchVendorFragment()
        fragInfo.arguments = bundle
        return fragInfo
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
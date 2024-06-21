package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.clone
import com.bridesandgrooms.event.MVP.VendorsAllPresenter
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.VendorAdapter
import com.bridesandgrooms.event.UI.ViewAnimation
import com.bridesandgrooms.event.databinding.VendorsAllBinding
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale

class VendorsAll : Fragment(), VendorsAllPresenter.VAVendors, FragmentActionListener {

    private lateinit var recyclerViewAllVendor: RecyclerView
    private lateinit var presentervendor: VendorsAllPresenter
    private lateinit var rvAdapter: VendorAdapter
    private lateinit var inf: VendorsAllBinding
    private lateinit var toolbar: MaterialToolbar

    private var vendorList = ArrayList<VendorPayment>()
    private var isRotate = false
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendors_menu, menu)
        val addVendor = menu.findItem(R.id.add_vendor)
        addVendor.isVisible = false

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.setOnSearchClickListener {}
        searchView.setOnCloseListener {
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList = ArrayList<VendorPayment>()
                filter(vendorList, p0).forEach { vendor->filteredModelList.add(VendorPayment(vendor.vendor, vendor.amountlist)) }
                rvAdapter = VendorAdapter(this@VendorsAll, filteredModelList, mContext!!)
                recyclerViewAllVendor.adapter = rvAdapter
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.vendors)
        inf = DataBindingUtil.inflate(inflater, R.layout.vendors_all, container, false)

        try {
            presentervendor = VendorsAllPresenter(mContext!!, this)
            presentervendor.getVendorList()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }

        ViewAnimation.init(inf.NewVendor)
        ViewAnimation.init(inf.ContactVendor)

        inf.floatingActionButtonVendor.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonVendor, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(inf.NewVendor)
                ViewAnimation.showIn(inf.ContactVendor)
            } else {
                ViewAnimation.showOut(inf.NewVendor)
                ViewAnimation.showOut(inf.ContactVendor)
            }
        }

        inf.fabNewVendor.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Add_Vendor")
            val fragment = VendorCreateEdit()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
                .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                .commit()
        }

        inf.fabContactVendor.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Search_Vendor")

            val fragment = SearchVendorTab()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
                .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                .commit()
        }
        return inf.root
    }


    private fun filter(models: ArrayList<VendorPayment>, query: String?): List<VendorPayment> {
        val lowerCaseQuery = query!!.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<VendorPayment> = ArrayList()
        for (model in models) {
            val text: String = model.vendor.name.toLowerCase(Locale.ROOT)
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    /**
     * Callback that loads a recyclerview with a list of Vendors when they are successfully retrieved from the Backend
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onVAVendors(
        vendorpaymentlist: ArrayList<VendorPayment>
    ) {
        if (vendorpaymentlist.size != 0) {
            vendorList = clone(vendorpaymentlist)
            recyclerViewAllVendor = inf.recyclerViewVendors
            recyclerViewAllVendor.apply {
                layoutManager = LinearLayoutManager(inf.root.context).apply {
                    reverseLayout = true
                }
            }
            vendorpaymentlist.sortedBy { it.vendor.category }

            try {
                rvAdapter = VendorAdapter(this, vendorpaymentlist, mContext!!)
                rvAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                println(e.message)
            }
            recyclerViewAllVendor.adapter = null
            recyclerViewAllVendor.adapter = rvAdapter
        }
    }

    /**
     * Callback that loads an emptystate fragment whenever the app cannot retrieve Vendors, in this case we are assuming that's because there are none. The fragment allows the user to add new Vendors
     */
    override fun onVAVendorsError(errcode: String) {
        val message = getString(R.string.emptystate_novendorsmsg)
        val cta = getString(R.string.emptystate_novendorscta)
        val actionClass =
            VendorCreateEdit::class.java
        val fragment = EmptyStateFragment.newInstance(message, cta, actionClass)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }

    companion object {
        const val SCREEN_NAME = "Vendors All"
        const val TAG = "VendorsAll"
    }

    /**
     * Whenever a Vendor selection is made and the User clicks on the ViewHolder in the RecyclerView this function will be called in VendorsAll to open the Vendor edition fragment
     */
    override fun onVendorFragmentWithData(vendor: Vendor) {
        val fragment = VendorCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("vendor", vendor)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
            //.addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
            .commit()
    }
}

interface FragmentActionListener {
    fun onVendorFragmentWithData(vendor: Vendor)
}

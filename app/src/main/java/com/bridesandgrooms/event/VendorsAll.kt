package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.VendorDeletionException
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.deleteVendor
import com.bridesandgrooms.event.MVP.VendorsAllPresenter
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.UI.Adapters.VendorAdapter
import com.bridesandgrooms.event.UI.Fragments.EmptyStateFragment
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.bridesandgrooms.event.databinding.VendorsAllBinding
import com.bridesandgrooms.event.UI.ViewAnimation
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class VendorsAll : Fragment(), VendorsAllPresenter.VAVendors, FragmentActionListener {

    private lateinit var recyclerViewAllVendor: RecyclerView
    private lateinit var presentervendor: VendorsAllPresenter
    private lateinit var rvAdapter: VendorAdapter
    private lateinit var inf: VendorsAllBinding
    private var isRotate = false
    private var mContext: Context? = null

//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                //val intent = result.data
//                try {
//                    presentervendor = VendorsAllPresenter(mContext!!, this)
//                    presentervendor.getVendorList()
//                } catch (e: Exception) {
//                    Log.e(TAG, e.message.toString())
//                }
//            }
//        }

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

//            val newvendor = Intent(context, VendorCreateEdit::class.java)
//            newvendor.putExtra("userid", "")
//            startForResult.launch(newvendor)
        }

        inf.fabContactVendor.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Search_Vendor")

//            val newvendor = Intent(context, ContactsAll::class.java)
//            newvendor.putExtra("vendorid", "")
//            startForResult.launch(newvendor)
        }
        return inf.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onVAVendors(
        vendorpaymentlist: ArrayList<VendorPayment>
    ) {
        if (vendorpaymentlist.size != 0) {
            recyclerViewAllVendor = inf.recyclerViewVendors
            recyclerViewAllVendor.apply {
                layoutManager = LinearLayoutManager(inf.root.context).apply {
                    reverseLayout = true
                }
            }

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
        const val DELETEACTION = "delete"
    }

    override fun onVendorFragmentWithData(vendor: Vendor) {
        val fragment = VendorCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("vendor", vendor)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
            .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
            .commit()
    }
}

interface FragmentActionListener {
    fun onVendorFragmentWithData(vendor: Vendor)
}

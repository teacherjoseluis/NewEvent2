package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.SearchView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.clone
import com.example.newevent2.MVP.GuestsAllPresenter
import com.example.newevent2.MVP.VendorsAllPresenter
import com.example.newevent2.Model.*
import com.example.newevent2.ui.ViewAnimation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.contacts.*
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.guests_all.*
import kotlinx.android.synthetic.main.guests_all.view.*
import kotlinx.android.synthetic.main.vendors_all.*
import kotlinx.android.synthetic.main.vendors_all.view.*


class VendorsAll : Fragment(), VendorsAllPresenter.VAVendors {

    var contactlist = ArrayList<com.example.newevent2.Model.Vendor>()
    private var isRotate = false

    lateinit var recyclerViewAllVendor: RecyclerView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presentervendor: VendorsAllPresenter
    private lateinit var inf: View
    private lateinit var rvAdapter: Rv_VendorAdapter
    private lateinit var swipeController: SwipeControllerTasks

    private var recyclerViewReadyCallback: RecyclerViewReadyCallback? = null

    private val autocomplete_place_code = 1
    private var placeid: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var address: String? = null
    private var phone: String? = null
    private var web: String? = null
    private var rating = 0.0
    private var userrating = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = activity!!.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendors_menu, menu)

        val addVendor = menu.findItem(R.id.add_vendor)
        addVendor.isVisible = false

//        val searchItem = menu.findItem(R.id.action_search)
//        val searchView = searchItem.actionView as SearchView
//        searchView.isIconified = false
//
//        searchView.setOnCloseListener {
//            toolbar.collapseActionView()
//            true
//        }
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                val filteredModelList= filter(contactlist, p0)
//                val rvAdapter = Rv_VendorAdapter(filteredModelList as ArrayList<Vendor>, context!!)
//                recyclerViewAllVendor.adapter = rvAdapter
//                return true
//            }
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf = inflater.inflate(R.layout.vendors_all, container, false)

        recyclerViewAllVendor = inf.recyclerViewVendors
        recyclerViewAllVendor.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        presentervendor = VendorsAllPresenter(context!!, this, inf)

        ViewAnimation.init(inf.NewVendor)
        ViewAnimation.init(inf.ContactVendor)

        inf.floatingActionButtonVendor.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonVendor, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(NewVendor)
                ViewAnimation.showIn(ContactVendor)
            } else {
                ViewAnimation.showOut(NewVendor)
                ViewAnimation.showOut(ContactVendor)
            }
        }

        inf.fabNewVendor.setOnClickListener {
            val newvendor = Intent(context, VendorCreateEdit::class.java)
            newvendor.putExtra("userid", "")
            startActivity(newvendor)
        }

//        inf.fabGoogleVendor.setOnClickListener {
//            val newvendor = Intent(context, MapsActivity::class.java)
//            startActivityForResult(newvendor, autocomplete_place_code)
//        }

        inf.fabContactVendor.setOnClickListener {
            val newvendor = Intent(context, ContactsAll::class.java)
            newvendor.putExtra("vendorid", "")
            startActivity(newvendor)
        }
        return inf
    }

    override fun onResume() {
        super.onResume()
////Just want to enter here after a new guest was added not every time. I don't like this.

        if (vendorcreated_flag == 1){
//            presenterguest = GuestsAllPresenter(context!!, this, inf)

            val vendordb = VendorDBHelper(context!!)
            val vendorlist = vendordb.getAllVendors()

            //rvAdapter = Rv_VendorAdapter(vendorlist, context!!)
//            recyclerViewAllGuests.adapter = null
            recyclerViewAllVendor.adapter = rvAdapter
            contactlist = clone(vendorlist)!!

//            swipeController = SwipeControllerTasks(
//                context!!,
//                rvAdapter,
//                recyclerViewAllGuests,
//                null,
//                RIGHTACTION
//            )
//            val itemTouchHelper = ItemTouchHelper(swipeController)
//            itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)

            vendorcreated_flag = 0
        }

    }

    private fun filter(models: ArrayList<Vendor>, query: String?): List<Vendor> {
        val lowerCaseQuery = query!!.toLowerCase()
        val filteredModelList: ArrayList<Vendor> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase()
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    override fun onVAVendors(
        inflatedView: View,
        list: ArrayList<VendorPayment>
    ) {
        rvAdapter = Rv_VendorAdapter(list, context!!)

        recyclerViewAllVendor.adapter = rvAdapter
        //contactlist = clone(list)!!

        swipeController = SwipeControllerTasks(
            inflatedView.context,
            rvAdapter,
            recyclerViewAllVendor,
            null,
            RIGHTACTION
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerViewAllVendor)
    }

    override fun onVAVendorsError(inflatedView: View, errcode: String) {
        inflatedView.withdatav.visibility = ConstraintLayout.GONE
        inflatedView.withnodatav.visibility = ConstraintLayout.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == autocomplete_place_code) {
            val placenameString = data?.getStringExtra("place_name")
            //          eventkey = data?.getStringExtra("eventid").toString()
            placeid = data?.getStringExtra("place_id").toString()
            latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            longitude = data!!.getDoubleExtra("place_longitude", 0.0)
            address = data?.getStringExtra("place_address").toString()
            phone = data?.getStringExtra("place_phone").toString()
            web = data?.getStringExtra("place_web").toString()
            rating = data?.getDoubleExtra("place_rating", 0.0)
            userrating = data?.getIntExtra("place_userrating", 0)

            val newvendor = Intent(activity, NewVendor::class.java)
//            newvendor.putExtra("eventkey", eventkey)
            newvendor.putExtra("location", placenameString)
            newvendor.putExtra("placeid", placeid)
            newvendor.putExtra("latitude", latitude)
            newvendor.putExtra("longitude", longitude)
            newvendor.putExtra("address", address)
            newvendor.putExtra("phone", phone)
            newvendor.putExtra("web", web)
            newvendor.putExtra("rating", rating)
            newvendor.putExtra("userrating", userrating)

            newvendor.putExtra("source", "google")

            startActivity(newvendor)
            //etlocation.setText(placenameString)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
        const val RIGHTACTION = "delete"
        internal val VENDORCREATION = 1
        var vendorcreated_flag = 0
    }
}

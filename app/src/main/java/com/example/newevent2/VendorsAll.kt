package com.example.newevent2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.clone
import com.example.newevent2.MVP.TableGuestsActivityPresenter
import com.example.newevent2.MVP.VendorsAllPresenter
import com.example.newevent2.Model.*
import com.example.newevent2.ui.ViewAnimation
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.vendors_all.*
import kotlinx.android.synthetic.main.vendors_all.view.*

class VendorsAll : Fragment(), VendorsAllPresenter.VAVendors {

    private lateinit var recyclerViewAllVendor: RecyclerView
    private lateinit var presentervendor: VendorsAllPresenter
    private lateinit var rvAdapter: Rv_VendorAdapter
    private lateinit var swipeController: SwipeControllerTasks

    private val autocompleteplacecode = 1
    private var placeid: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var address: String? = null
    private var phone: String? = null
    private var web: String? = null
    private var rating = 0.0
    private var userrating = 0
    private var isRotate = false

    private var contactlist = ArrayList<Vendor>()

    private val REQUEST_CODE_CONTACTS = 1
    private val REQUEST_CODE_VENDOR = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendors_menu, menu)

        //By default the menu is set as invisible, if the vendor already exists
        // (and it's an edit operation) the menu will be back to visibility
        val addVendor = menu.findItem(R.id.add_vendor)
        addVendor.isVisible = false
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

        // Invoking the presenter that will populate the recyclerview
        presentervendor = VendorsAllPresenter(requireContext(), this)

        //This is for the Add button, Vendors can be added from scratch or from the contact list
        ViewAnimation.init(inf.NewVendor)
        ViewAnimation.init(inf.ContactVendor)

        inf.floatingActionButtonVendor.setOnClickListener()
        {
            //When it's clicked on, the button will rotate and show the additional options
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
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWVENDOR")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            // Call to the form to create vendors
            val newvendor = Intent(context, VendorCreateEdit::class.java)
            newvendor.putExtra("userid", "")
            startActivityForResult(newvendor, REQUEST_CODE_VENDOR)
        }

        inf.fabContactVendor.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "VENDORFROMCONTACTS")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            // Call to the contact list from which vendors can added from the contacts in the phone
            val newvendor = Intent(context, ContactsAll::class.java)
            newvendor.putExtra("vendorid", "")
            startActivityForResult(newvendor, REQUEST_CODE_CONTACTS)
        }
        return inf
    }

    override fun onResume() {
        super.onResume()
        // Not really sure about keeping with this option. This is suppose
        // to refresh the list whenever the user comes back but I think it's not working properly
        if (vendorcreated_flag == 1){
            val vendordb = VendorDBHelper(requireContext())
            val vendorlist = vendordb.getAllVendors()
            recyclerViewAllVendor.adapter = rvAdapter
            contactlist = clone(vendorlist)
            vendorcreated_flag = 0
        }
    }

    override fun onVAVendors(
        //inflatedView: View,
        vendorpaymentlist: ArrayList<VendorPayment>
    ) {
        // There are vendors obtained from the presenter and these are passed to the recyclerview
        rvAdapter = Rv_VendorAdapter(vendorpaymentlist, requireContext())

        recyclerViewAllVendor.adapter = null
        recyclerViewAllVendor.adapter = rvAdapter

//        swipeController = SwipeControllerTasks(
//            inflatedView.context,
//            rvAdapter,
//            recyclerViewAllVendor,
//            null,
//            RIGHTACTION
//        )
//        // Adding the Swipe capabilities to the recyclerview
//        val itemTouchHelper = ItemTouchHelper(swipeController)
//        itemTouchHelper.attachToRecyclerView(recyclerViewAllVendor)
    }

    override fun onVAVendorsError(errcode: String) {
        // No vendors coming, the regular layout is hidden and the emptystate one is shown
        withdatav.visibility = ConstraintLayout.GONE
        withnodatav.visibility = ConstraintLayout.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //This is when it's coming from the Google's search for a Business,
        // if the user selects a business that is a provider for the event, all of its data
        // is dumped here an used to be saved as a Vendor's item
        if (resultCode == Activity.RESULT_OK && requestCode == autocompleteplacecode) {
            placeid = data?.getStringExtra("place_id").toString()
            latitude = data!!.getDoubleExtra("place_latitude", 0.0)
            longitude = data.getDoubleExtra("place_longitude", 0.0)
            address = data.getStringExtra("place_address").toString()
            phone = data.getStringExtra("place_phone").toString()
            web = data.getStringExtra("place_web").toString()
            rating = data.getDoubleExtra("place_rating", 0.0)
            userrating = data.getIntExtra("place_userrating", 0)

//          I think this code is never called
//            val newvendor = Intent(activity, NewVendor::class.java)
//            newvendor.putExtra("location", placenameString)
//            newvendor.putExtra("placeid", placeid)
//            newvendor.putExtra("latitude", latitude)
//            newvendor.putExtra("longitude", longitude)
//            newvendor.putExtra("address", address)
//            newvendor.putExtra("phone", phone)
//            newvendor.putExtra("web", web)
//            newvendor.putExtra("rating", rating)
//            newvendor.putExtra("userrating", userrating)
//            newvendor.putExtra("source", "google")
//            startActivity(newvendor)
        }
        if (((requestCode == REQUEST_CODE_CONTACTS) || (requestCode == REQUEST_CODE_VENDOR)) && resultCode == Activity.RESULT_OK) {
            //val guestarray = data?.getSerializableExtra("guests") as ArrayList<Guest>
            presentervendor = VendorsAllPresenter(requireContext(), this)
        }
    }

    companion object {
        const val RIGHTACTION = "delete"
        var vendorcreated_flag = 0
    }
}

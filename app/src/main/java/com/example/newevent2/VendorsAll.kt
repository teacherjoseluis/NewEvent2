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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.ui.ViewAnimation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.contacts.*
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.vendors_all.*
import kotlinx.android.synthetic.main.vendors_all.view.*


class VendorsAll : Fragment() {

    var contactlist = ArrayList<Vendor>()
    var eventkey: String? = null
    lateinit var recyclerViewAllVendor: RecyclerView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

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

    var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = activity!!.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)


        activity!!.floatingActionButtonVendor.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(activity!!.floatingActionButtonVendor, !isRotate)
            if(isRotate){
                ViewAnimation.showIn(activity!!.GoogleLayout);
                ViewAnimation.showIn(activity!!.LocalLayout);
            }else{
                ViewAnimation.showOut(activity!!.GoogleLayout);
                ViewAnimation.showOut(activity!!.LocalLayout);
            }
        }

        activity!!.fabGoogle.setOnClickListener(View.OnClickListener {
            val newvendor = Intent(activity, MapsActivity::class.java)
            startActivityForResult(newvendor, autocomplete_place_code)
        })

        activity!!.fabLocal.setOnClickListener(View.OnClickListener {
            val newvendor = Intent(activity, NewVendor::class.java)
            newvendor.putExtra("source", "local")
            startActivity(newvendor)
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendors_menu, menu)


        val sortItem = menu.findItem(R.id.action_sortvendor)
        sortItem.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sortvendor -> {
                    contactlist.sortWith(Comparator { object1, object2 ->
                        object2.name.compareTo(
                            object1.name
                        )
                    })
                    recyclerViewAllVendor.adapter?.notifyDataSetChanged()
                }
            }
            true
        }

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false

        searchView.setOnCloseListener {
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList: List<Vendor> = filter(contactlist, p0)
                val rvAdapter = Rv_VendorAdapter(filteredModelList as MutableList<Vendor>)
                recyclerViewAllVendor.adapter = rvAdapter
                return true
            }
        })
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.vendors_all, container, false)

        recyclerViewAllVendor = inf.recyclerViewVendors
        recyclerViewAllVendor.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }


        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_CONTACTS
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                readvendors()
            }
        }
        return inf
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    readvendors()
                } else {
                    //permission from popup denied
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //private fun readvendors(eventkey: String) {
    private fun readvendors() {
        val contentResolver = context!!.contentResolver
        val vendorentity = VendorEntity()
        //vendorentity.eventid = eventkey
        vendorentity.getVendorsContacts(object : FirebaseSuccessListener_Vendor {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onVendorList(list: ArrayList<Vendor>) {
                contactlist.clear()

                //val contentResolver = context!!.contentResolver

                for (vendor in list) {

                    val whereclause = StringBuffer()
                    whereclause.append(ContactsContract.Contacts._ID)
                    whereclause.append(" = ")
                    whereclause.append(vendor.contactid)

                    var contactname: String? = null
                    var contactphoto: String? = null
                    var cursor: Cursor? = null

                    if (vendor.contactid != "local" && vendor.contactid != "google") {
                        cursor =
                            contentResolver.query(
                                ContactsContract.Contacts.CONTENT_URI,
                                null,
                                whereclause.toString(),
                                null, null
                            )

                        cursor?.moveToNext()
                        contactname =
                            cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                        contactphoto =
                            cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                    }

                    val contactitem = Vendor()
                    contactitem.name = contactname?.let { it } ?: vendor.name
                    contactitem.contactid = vendor.contactid
                    //contactitem.eventid = vendor.eventid
//                    val imageuri = contactphoto?.let { it } ?: vendor.imageurl
//                    if (imageuri.isNullOrEmpty()) {
//                        contactitem.imageurl = Uri.parse(
//                            ContentResolver.SCHEME_ANDROID_RESOURCE +
//                                    "://" + resources.getResourcePackageName(R.drawable.avatar2)
//                                    + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
//                                R.drawable.avatar2
//                            )
//                        ).toString()
//                    } else {
//                        contactitem.imageurl = imageuri
//                    }
                    contactitem.key = vendor.key
                    contactitem.phone = vendor.phone
                    contactitem.email = vendor.email
                    contactitem.location = vendor.location
                    contactitem.latitude = vendor.latitude
                    contactitem.longitude = vendor.longitude
                    contactlist.add(contactitem)
                    cursor?.let { cursor.close() }
                }

//                contactlist.sortWith(Comparator { object1, object2 -> object2.name.compareTo(object1.name) })

                val rvAdapter = Rv_VendorAdapter(contactlist)
                recyclerViewAllVendor.adapter = rvAdapter

                recyclerViewAllVendor.viewTreeObserver
                    .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            if (recyclerViewReadyCallback != null) {
                                recyclerViewReadyCallback!!.onLayoutReady()
                            }
                            recyclerViewAllVendor.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })

                recyclerViewReadyCallback = object : RecyclerViewReadyCallback {
                    override fun onLayoutReady() {
                        val swipeController =
                            SwipeControllerTasks(context!!, rvAdapter, recyclerViewVendors,null,"delete")
                        val itemTouchHelper = ItemTouchHelper(swipeController)
                        itemTouchHelper.attachToRecyclerView(recyclerViewVendors)
                    }
                }
            }
        })
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

}

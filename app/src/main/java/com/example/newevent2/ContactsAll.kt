package com.example.newevent2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.*
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.addVendor
import com.example.newevent2.Functions.contacttoGuest
import com.example.newevent2.Functions.contacttoVendor
import com.example.newevent2.MVP.ContactsAllPresenter
import com.example.newevent2.Model.Contact
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.Vendor
import kotlinx.android.synthetic.main.contacts_all.*
import kotlinx.android.synthetic.main.contacts_all.view.*
import kotlinx.android.synthetic.main.vendors_all.view.*
import java.util.*
import kotlin.collections.ArrayList

class ContactsAll : AppCompatActivity(), ContactsAllPresenter.GAContacts, CoRAddEditGuest,
    CoRAddEditVendor {

    private var contactlist = ArrayList<Contact>()
    private lateinit var activitymenu: Menu
    private lateinit var recyclerViewAllContacts: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: ContactsAllPresenter
    private lateinit var apptitle: TextView
    private lateinit var rvAdapter: Rv_ContactAdapter
    private lateinit var loadingview: View
    private lateinit var withdataview: View
    lateinit var mContext: Context

    private var callerclass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts_all)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // There are different behaviors whether this is a Guest or Vendor calling the class
        val extras = intent.extras
        callerclass = if (extras!!.containsKey("guestid")) {
            "guest"
        } else {
            "vendor"
        }

        //Declaring the title and layout views
        apptitle = findViewById(R.id.appbartitle)
        apptitle.text = getString(R.string.title_contacts)

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

        //Checking for permissions to read the contacts information
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_DENIED
        ) {
            //permission denied
            val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
            //show popup to request runtime permission
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            //permission already granted
            // Call the presenter
            presenterguest = ContactsAllPresenter(this, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.guests_menu, menu)

        //This header will have the ability to filter contacts based on user's input
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
                val filteredModelList = filter(contactlist, p0)
                //The list is updated based on this search criteria
                rvAdapter = Rv_ContactAdapter(filteredModelList as ArrayList<Contact>)
                recyclerViewAllContacts.adapter = rvAdapter
                return true
            }
        })
        //By default menus are disabled
        activitymenu = menu
        val guestmenu = activitymenu.findItem(R.id.add_guest)
        guestmenu.isEnabled = false
        val vendormenu = activitymenu.findItem(R.id.add_vendor)
        vendormenu.isEnabled = false
        return true
    }

    //Filter function needed to search for specific elements in the contact list
    private fun filter(models: ArrayList<Contact>, query: String?): List<Contact> {
        val lowerCaseQuery = query!!.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<Contact> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase(Locale.ROOT)
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    override fun onGAContacts(list: ArrayList<Contact>) {
        recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactsAll).apply {
                stackFromEnd = true
                reverseLayout = true
            }
            rvAdapter = Rv_ContactAdapter(list)
            recyclerViewAllContacts = recyclerViewContacts
            recyclerViewAllContacts.adapter = rvAdapter
            contactlist = clone(list)

            if (callerclass == "guest") {
                rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                        if (countselected.size != 0) {
                            //Updates the appbar with the number of contacts selected
                            apptitle.text = "${countselected.size} selected"
                            val guestmenu = activitymenu.findItem(R.id.add_guest)
                            // Only then we can enable the Guest menu
                            guestmenu.isEnabled = true
                            guestmenu.setOnMenuItemClickListener {
                                //Enabling a loading view to allow the async call to comeback
                                loadingview.visibility = ConstraintLayout.VISIBLE
                                withdataview.visibility = ConstraintLayout.GONE
                                when (it.itemId) {
                                    R.id.add_guest -> {
                                        for (ind in countselected) {
                                            //Converting whatever contacts we selected to Guest items
                                            val guest =
                                                contacttoGuest(context, contactlist[ind].key)
                                            addGuest(context, guest, CALLER)
                                        }
                                        apptitle.text = getString(R.string.title_contacts)
                                        rvAdapter.onClearSelected()
                                    }
                                }
                                true
                            }
                        } else {
                            //Disable the menu as nothing is selected
                            apptitle.text = getString(R.string.title_contacts)
                            val guestmenu = activitymenu.findItem(R.id.add_guest)
                            guestmenu.isEnabled = false
                        }
                    }
                }
            } else if (callerclass == "vendor") {
                rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                        if (countselected.size != 0) {
                            //Updates the appbar with the number of elements selected
                            apptitle.text = "${countselected.size} selected"
                            val vendormenu = activitymenu.findItem(R.id.add_vendor)
                            //Enable the menu exclusive for vendors
                            vendormenu.isEnabled = true
                            vendormenu.setOnMenuItemClickListener {
                                loadingview.visibility = ConstraintLayout.VISIBLE
                                withdataview.visibility = ConstraintLayout.GONE
                                when (it.itemId) {
                                    R.id.add_vendor -> {
                                        for (ind in countselected) {
                                            //Converting the contacts to Vendors and adding them
                                            val vendor =
                                                contacttoVendor(context, contactlist[ind].key)
                                            addVendor(context, vendor, CALLER)
                                        }
                                        apptitle.text = getString(R.string.title_contacts)
                                        rvAdapter.onClearSelected()
                                    }
                                }
                                true
                            }
                        } else {
                            apptitle.text = getString(R.string.title_contacts)
                            val vendormenu = activitymenu.findItem(R.id.add_vendor)
                            vendormenu.isEnabled = false
                        }
                    }
                }
            }
        }
    }

    override fun onGAContactsError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onAddEditGuest(guest: Guest) {
        //Callbacks whenever adding the Guest ends,
        // this will hide the loading view and return to the normal layout
        (mContext as ContactsAll).loadingview.visibility = ConstraintLayout.GONE
        (mContext as ContactsAll).withdataview.visibility = ConstraintLayout.VISIBLE
        GuestsAll.guestcreated_flag = 1
    }

    override fun onAddEditVendor(vendor: Vendor) {
        //Callbacks whenever adding the Vendor ends,
        // this will hide the loading view and return to the normal layout
        (mContext as ContactsAll).loadingview.visibility = ConstraintLayout.GONE
        (mContext as ContactsAll).withdataview.visibility = ConstraintLayout.VISIBLE
        VendorsAll.vendorcreated_flag = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    presenterguest = ContactsAllPresenter(this, this)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        internal const val PERMISSION_CODE = 1001
        const val CALLER = "contact"
    }
}


package com.bridesandgrooms.event

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.addVendor
import com.bridesandgrooms.event.Functions.contacttoGuest
import com.bridesandgrooms.event.Functions.contacttoVendor
import com.bridesandgrooms.event.MVP.ContactsAllPresenter
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.UI.OnItemClickListener
import com.bridesandgrooms.event.databinding.ContactsAllBinding
import com.google.android.material.chip.Chip
//import kotlinx.android.synthetic.main.contacts_all.*
//import kotlinx.android.synthetic.main.contacts_all.view.*
//import kotlinx.android.synthetic.main.vendors_all.view.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ContactsAll : AppCompatActivity(), ContactsAllPresenter.GAContacts, CoRAddEditGuest,
    CoRAddEditVendor {

    private var contactlist = ArrayList<Contact>()
    private var guestlist = ArrayList<Guest>()

    private lateinit var activitymenu: Menu
    private lateinit var recyclerViewAllContacts: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: ContactsAllPresenter
    private lateinit var apptitle: TextView
    private lateinit var rvAdapter: Rv_ContactAdapter
    //private lateinit var loadingview: View
    //private lateinit var withdataview: View
    //private lateinit var permissionsview: View
    private lateinit var binding: ContactsAllBinding
    lateinit var mContext: Context

    private var callerclass = ""

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                // Permission is granted. Continue the action or workflow in your
//                // app.
//                try {
//                    presenterguest = ContactsAllPresenter(this, this)
//                } catch (e: Exception) {
//                    println(e.message)
//                }
//            } else {
//                // Explain to the user that the feature is unavailable because the
//                // feature requires a permission that the user has denied. At the
//                // same time, respect the user's decision. Don't link to system
//                // settings in an effort to convince the user to change their
//                // decision.
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.contacts_all)
        binding = DataBindingUtil.setContentView(this, R.layout.contacts_all)

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

        //loadingview = findViewById(R.id.loadingscreen)
        //withdataview = findViewById(R.id.withdata)
        //permissionsview = findViewById(R.id.permissions)

        //Checking for permissions to read the contacts information
        if (!PermissionUtils.checkPermissions(applicationContext, "contact")) {
            PermissionUtils.alertBox(this, "contact")
        } else {
            //permission already granted
            // Call the presenter
            try {
                presenterguest = ContactsAllPresenter(this, this)
            } catch (e: Exception) {
                println(e.message)
            }
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
                binding.recyclerViewContacts.adapter = rvAdapter
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

    //-- Need to migrate to Databinding:
    override fun onGAContacts(list: ArrayList<Contact>) {
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactsAll).apply {
                stackFromEnd = true
                reverseLayout = true
            }
            rvAdapter = Rv_ContactAdapter(list)
            //recyclerViewAllContacts = binding.recyclerViewContacts
            binding.recyclerViewContacts.adapter = rvAdapter
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
                                //31-May - turning off
                                //loadingview.visibility = ConstraintLayout.VISIBLE
                                //withdataview.visibility = ConstraintLayout.GONE
                                when (it.itemId) {
                                    R.id.add_guest -> {
                                        for (ind in countselected) {
                                            //Converting whatever contacts we selected to Guest items
                                            val guest =
                                                contacttoGuest(context, contactlist[ind].key)
                                            guestlist.add(guest)
                                            lifecycleScope.launch {
                                                addGuest(context, guest, CALLER)
                                            }
                                        }
                                        apptitle.text = getString(R.string.title_contacts)
                                        rvAdapter.onClearSelected()
                                    }
                                }
                                Thread.sleep(1500)
                                finish()
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
                                binding.loadingscreen.root.visibility = ConstraintLayout.VISIBLE
                                binding.withdata.visibility = ConstraintLayout.GONE
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
                                Thread.sleep(1500)
                                finish()
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
        binding.loadingscreen.root.visibility = ConstraintLayout.GONE
        binding.withdata.visibility = ConstraintLayout.VISIBLE
        GuestsAll.guestcreated_flag = 1
    }

    override fun onAddEditVendor(vendor: Vendor) {
        //Callbacks whenever adding the Vendor ends,
        // this will hide the loading view and return to the normal layout
        binding.loadingscreen.root.visibility = ConstraintLayout.GONE
        binding.withdata.visibility = ConstraintLayout.VISIBLE
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
                    try {
                        presenterguest = ContactsAllPresenter(this, this)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                else {
                    // Here goes what happens when the permission is not given
                    binding.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val contactpermissions = Permission.getPermission("contact")
                    val resourceId = this.resources.getIdentifier(
                        contactpermissions.drawable, "drawable",
                        this.packageName
                    )
                    binding.permissions.root.findViewById<ImageView>(R.id.permissionicon).setImageResource(resourceId)

                    val language = this.resources.configuration.locales.get(0).language
                    val permissionwording = when (language) {
                            "en" -> contactpermissions.permission_wording_en
                            else -> contactpermissions.permission_wording_es
                        }
                    binding.permissions.root.findViewById<TextView>(R.id.permissionwording).text = permissionwording

                    val openSettingsButton = binding.permissions.root.findViewById<Button>(R.id.permissionsbutton)
                    openSettingsButton.setOnClickListener {
                        // Create an intent to open the app settings for your app
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val packageName = packageName
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri

                        // Start the intent
                        startActivity(intent)
                    }
                }
            }
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun finish() {
        val returnintent = Intent()
        val returnbundle = Bundle()
        returnbundle.putSerializable("guests", guestlist)
        returnintent.putExtras(returnbundle)
        setResult(RESULT_OK, returnintent)
        super.finish()
    }

    companion object {
        internal const val PERMISSION_CODE = 1001
        const val CALLER = "contact"
    }
}


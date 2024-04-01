package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.GuestCreationException
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.addVendor
import com.bridesandgrooms.event.MVP.ContactsAllPresenter
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.UI.OnItemClickListener
import com.bridesandgrooms.event.databinding.ContactsAllBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
//import kotlinx.android.synthetic.main.contacts_all.*
//import kotlinx.android.synthetic.main.contacts_all.view.*
//import kotlinx.android.synthetic.main.vendors_all.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ContactsAll : AppCompatActivity(), ContactsAllPresenter.GAContacts {

    private var contactlist = ArrayList<Contact>()
    private var guestlist = ArrayList<Guest>()

    private lateinit var activitymenu: Menu
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: ContactsAllPresenter
    private lateinit var apptitle: TextView
    private lateinit var rvAdapter: Rv_ContactAdapter
    private lateinit var binding: ContactsAllBinding

    private lateinit var userSession: User

    private var callerclass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.contacts_all)
        userSession = User().getUser(this)
        binding = DataBindingUtil.setContentView(this, R.layout.contacts_all)

        apptitle = findViewById(R.id.appbartitle)
        apptitle.text = getString(R.string.title_contacts)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val extras = intent.extras
        callerclass = if (extras!!.containsKey("guestid")) {
            "guest"
        } else {
            "vendor"
        }

        if (!PermissionUtils.checkPermissions(this, "contact")) {
            val permissions = PermissionUtils.requestPermissionsList("contact")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            try {
                presenterguest = ContactsAllPresenter(this, this)
                presenterguest.getContactsList()
            } catch (e: Exception) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "ContactsAllPresenter",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.guests_menu, menu)
        activitymenu = menu
        activitymenu.findItem(R.id.add_guest).isEnabled = false
        activitymenu.findItem(R.id.add_vendor).isEnabled = false
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
                rvAdapter = Rv_ContactAdapter(filteredModelList as ArrayList<Contact>)
                binding.recyclerViewContacts.adapter = rvAdapter
                return true
            }
        })
        return true
    }

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
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactsAll).apply {
                stackFromEnd = true
                reverseLayout = true
            }
            rvAdapter = Rv_ContactAdapter(list)
            binding.recyclerViewContacts.adapter = rvAdapter
            contactlist = clone(list)

            when (callerclass) {
                "guest" -> {
                    rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                        @SuppressLint("SetTextI18n")
                        override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                            AnalyticsManager.getInstance()
                                .trackUserInteraction(SCREEN_NAME, "Add_Guest")
                            if (countselected.size != 0) {
                                apptitle.text = "${countselected.size} selected"
                                activitymenu.findItem(R.id.add_guest).apply {
                                    isEnabled = true
                                    setOnMenuItemClickListener {
                                        when (it.itemId) {
                                            R.id.add_guest -> {
                                                apptitle.text = getString(R.string.title_contacts)
                                                for (ind in countselected) {
                                                    val guest =
                                                        Guest().contacttoGuest(
                                                            context,
                                                            contactlist[ind].key
                                                        )
                                                    guestlist.add(guest)
                                                    //lifecycleScope.launch {
                                                        try {
                                                            addGuest(context, userSession, guest)
                                                        } catch (e: GuestCreationException) {
                                                            AnalyticsManager.getInstance()
                                                                .trackError(
                                                                    SCREEN_NAME,
                                                                    e.message.toString(),
                                                                    "addGuest()",
                                                                    e.stackTraceToString()
                                                                )
                                                            Log.e(TAG, e.message.toString())
                                                            //withContext(Dispatchers.Main) {
                                                                displayToastMsg(getString(R.string.errorGuestCreation) + e.toString())
                                                            //}
                                                        }
                                                    //}
                                                }
                                                rvAdapter.onClearSelected()
                                            }
                                        }
                                        //Thread.sleep(1500)
                                        finish()
                                        true
                                    }
                                }
                            } else {
                                //Disable the menu as nothing is selected
                                apptitle.text = getString(R.string.title_contacts)
                                activitymenu.findItem(R.id.add_guest).isEnabled = false
                            }
                        }
                    }

                }

                "vendor" -> {
                    rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                        @SuppressLint("SetTextI18n")
                        override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                            AnalyticsManager.getInstance()
                                .trackUserInteraction(SCREEN_NAME, "Add_Vendor")
                            if (countselected.size != 0) {
                                apptitle.text = "${countselected.size} selected"
                                activitymenu.findItem(R.id.add_vendor).apply {
                                    isEnabled = true
                                    setOnMenuItemClickListener {
                                        binding.loadingscreen.root.visibility =
                                            ConstraintLayout.VISIBLE
                                        binding.withdata.visibility = ConstraintLayout.GONE
                                        when (it.itemId) {
                                            R.id.add_vendor -> {
                                                apptitle.text = getString(R.string.title_contacts)
                                                for (ind in countselected) {
                                                    val vendor =
                                                        Vendor().contacttoVendor(
                                                            context,
                                                            contactlist[ind].key
                                                        )
                                                    //lifecycleScope.launch {
                                                        try {
                                                            addVendor(context, userSession, vendor)
                                                        } catch (e: GuestCreationException) {
                                                            AnalyticsManager.getInstance()
                                                                .trackError(
                                                                    SCREEN_NAME,
                                                                    e.message.toString(),
                                                                    "addVendor()",
                                                                    e.stackTraceToString()
                                                                )
                                                            Log.e(TAG, e.message.toString())
                                                            //withContext(Dispatchers.Main) {
                                                                displayToastMsg(getString(R.string.errorVendorCreation) + e.toString())
                                                            //}
                                                        }
                                                    //}
                                                }
                                                rvAdapter.onClearSelected()
                                            }
                                        }
                                        //Thread.sleep(1500)
                                        finish()
                                        true
                                    }
                                }
                            } else {
                                apptitle.text = getString(R.string.title_contacts)
                                activitymenu.findItem(R.id.add_vendor).isEnabled = false
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onGAContactsError(errcode: String) {
        TODO("Not yet implemented")
    }

//    override fun onAddEditGuest(context: Context, user: User, guest: Guest) {
//        binding.loadingscreen.root.visibility = ConstraintLayout.GONE
//        binding.withdata.visibility = ConstraintLayout.VISIBLE
//    }
//
//    override fun onAddEditVendor(context: Context, user: User, vendor: Vendor) {
//        binding.loadingscreen.root.visibility = ConstraintLayout.GONE
//        binding.withdata.visibility = ConstraintLayout.VISIBLE
//    }

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
                        presenterguest.getContactsList()
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                } else {
                    // Here goes what happens when the permission is not given
                    binding.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val contactpermissions = Permission.getPermission("contact")
                    val resourceId = this.resources.getIdentifier(
                        contactpermissions.drawable, "drawable",
                        this.packageName
                    )
                    binding.permissions.root.findViewById<ImageView>(R.id.permissionicon)
                        .setImageResource(resourceId)

                    val language = this.resources.configuration.locales.get(0).language
                    val permissionwording = when (language) {
                        "en" -> contactpermissions.permission_wording_en
                        else -> contactpermissions.permission_wording_es
                    }
                    binding.permissions.root.findViewById<TextView>(R.id.permissionwording).text =
                        permissionwording

                    val openSettingsButton =
                        binding.permissions.root.findViewById<Button>(R.id.permissionsbutton)
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

    private fun displayToastMsg(message: String) {
        Toast.makeText(
            this@ContactsAll,
            message,
            Toast.LENGTH_LONG
        ).show()
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
        const val SCREEN_NAME = "Contacts_All"
        const val TAG = "ContactsAll"
    }
}


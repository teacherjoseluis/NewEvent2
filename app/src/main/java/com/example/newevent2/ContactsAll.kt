package com.example.newevent2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.clone
import com.example.newevent2.Functions.contacttoGuest
import com.example.newevent2.MVP.ContactsAllPresenter
import com.example.newevent2.Model.Contact
import com.example.newevent2.Model.Guest
import kotlinx.android.synthetic.main.contacts_all.*
import kotlinx.android.synthetic.main.contacts_all.view.*
import kotlinx.android.synthetic.main.vendors_all.view.*

class ContactsAll : AppCompatActivity(), ContactsAllPresenter.GAContacts, CoRAddEditGuest {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts_all)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        apptitle = findViewById(R.id.appbartitle)
        apptitle.text = "Contacts"

        loadingview = findViewById(R.id.loadingscreen)
        withdataview = findViewById(R.id.withdata)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                presenterguest = ContactsAllPresenter(this, this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.guests_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false

        searchView.setOnSearchClickListener {

        }

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
                recyclerViewAllContacts.adapter = rvAdapter
                return true
            }
        })
        activitymenu = menu
        val guestmenu = activitymenu.findItem(R.id.add_guest)
        guestmenu.isEnabled = false
        return true
    }

    private fun filter(models: ArrayList<Contact>, query: String?): List<Contact> {
        val lowerCaseQuery = query!!.toLowerCase()
        val filteredModelList: ArrayList<Contact> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase()
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
            // rvAdapter.setHasStableIds(true)
            recyclerViewAllContacts = recyclerViewContacts
            recyclerViewAllContacts.adapter = rvAdapter
            //recyclerViewAllContacts.itemAnimator = null
            contactlist = clone(list)!!

            rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                @SuppressLint("SetTextI18n")
                override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                    //val apptitle = findViewById<TextView>(R.id.appbartitle)
                    if (countselected.size != 0) {
                        apptitle.text = "${countselected.size} selected"
                        val guestmenu = activitymenu.findItem(R.id.add_guest)
                        guestmenu.isEnabled = true
                        guestmenu.setOnMenuItemClickListener {
                            loadingview.visibility = ConstraintLayout.VISIBLE
                            withdataview.visibility = ConstraintLayout.GONE
                            when (it.itemId) {
                                R.id.add_guest -> {
                                    for (index in countselected) {
                                        val guest = contacttoGuest(context, contactlist[index].key)
                                        addGuest(context, guest)
                                    }
                                    apptitle.text = "Contacts"
                                    rvAdapter.onClearSelected()
                                    true
                                }
                            }
                            true
                        }
                    } else {
                        apptitle.text = "Contacts"
                        val guestmenu = activitymenu.findItem(R.id.add_guest)
                        guestmenu.isEnabled = false
                    }
                }
            }
        }
    }

    override fun onGAContactsError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onAddEditGuest(guest: Guest) {
        (mContext as ContactsAll).loadingview.visibility = ConstraintLayout.GONE
        (mContext as ContactsAll).withdataview.visibility = ConstraintLayout.VISIBLE
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
                    presenterguest = ContactsAllPresenter(this, this)
                } else {
                    //permission from popup denied
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        internal val GUEST_ADDED = 1
        internal val GUEST_NOT_ADDED = 0
        internal val PERMISSION_CODE = 1001
    }
}


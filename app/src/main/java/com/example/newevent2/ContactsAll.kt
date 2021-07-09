package com.example.newevent2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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


class ContactsAll : AppCompatActivity(), ContactsAllPresenter.GAContacts {

    private var contactlist = ArrayList<Contact>()
    private lateinit var activitymenu: Menu
    private lateinit var recyclerViewAllContacts: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: ContactsAllPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts_all)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

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
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.guests_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false

        searchView.setOnSearchClickListener {
            //           eventspinner.isEnabled = false
        }

        searchView.setOnCloseListener {
            //           eventspinner.isEnabled = true
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList = filter(contactlist, p0)
                val rvAdapter = Rv_GuestAdapter(filteredModelList as ArrayList<Guest>)
                recyclerViewAllContacts.adapter = rvAdapter
                return true
            }
        })

//        val addItem = menu.findItem(R.id.add_guest)
//        addItem.setOnMenuItemClickListener(object: MenuItem.OnMenuItemClickListener {
//            override fun onMenuItemClick(p0: MenuItem?): Boolean {
//                TODO("Not yet implemented")
//            }
//        })
        activitymenu = menu
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.add_guest -> {
//                for (index in countselected) {
//                    val guest = contacttoGuest(context, contactlist[index].key)
//                    addGuest(context, guest)
//                }
//                rvAdapter.onClearSelected()
//                toolbarmenuflag = false
//                toolbar.menu.clear()
//                true
//            }
//            return super.onOptionsItemSelected(item)
//        }
//    }

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
        var toolbarmenuflag = false
        recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactsAll).apply {
                stackFromEnd = true
                reverseLayout = true
            }
            val rvAdapter = Rv_ContactAdapter(list)
            recyclerViewAllContacts = recyclerViewContacts
            recyclerViewAllContacts.adapter = rvAdapter
            contactlist = clone(list)!!

            rvAdapter.mOnItemClickListener = object : OnItemClickListener {
                @SuppressLint("SetTextI18n")
                override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                    val appbartitle = findViewById<TextView>(R.id.appbartitle)
                    if (countselected.size != 0) {
                       // appbartitle.text = "${countselected.size} selected"

                        if (!toolbarmenuflag) {
                            //menuInflater.inflate(R.menu.contacts_menu, activitymenu)
                            //toolbar.inflateMenu(R.menu.contacts_menu)
                            toolbarmenuflag = true
                        }

                        val guestmenu = activitymenu.findItem(R.id.add_guest)

                        guestmenu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.add_guest -> {
                                    for (index in countselected) {
                                        val guest = contacttoGuest(context, contactlist[index].key)
                                        addGuest(context, guest)
                                    }
                                    rvAdapter.onClearSelected()
                                    toolbarmenuflag = false
                                    //toolbar.menu.clear()
                                    true
                                }
//                        toolbar.setOnMenuItemClickListener(androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
//                            when (it.itemId) {
//                                R.id.add_guest -> {
//                                    for (index in countselected) {
//                                        val guest = contacttoGuest(context, contactlist[index].key)
//                                        addGuest(context, guest)
//                                    }
//                                    rvAdapter.onClearSelected()
//                                    toolbarmenuflag = false
//                                    toolbar.menu.clear()
//                                    true
//                                }
//                                R.id.add_vendor -> {
//                                    for (index in countselected) {
//                                        val vendor = VendorEntity()
//                                        //vendor.eventid = key
//                                        vendor.contactid = contactlist[index].key
//                                        vendor.addVendor()
//                                    }
//                                    rvAdapter.onClearSelected()
//                                    appbartitle.text = "Contacts"
//                                    toolbarmenuflag = false
//                                    toolbar.menu.clear()
//                                    true
//                                }
                            }
                            true
//                        })
//                    } else {
//                        toolbarmenuflag = false
//                        toolbar.menu.clear()
//                    }
                        }
                    }
                    recyclerViewAllContacts.adapter = rvAdapter
                }
            }
        }
    }

    override fun onGAContactsError(errcode: String) {
        TODO("Not yet implemented")
    }

    companion object{
        internal val PERMISSION_CODE = 1001
    }
}

//
//    companion object {
//        //image pick code
//        private val IMAGE_PICK_CODE = 1000
//
//        //Permission code
//        private val PERMISSION_CODE = 1001
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED
//                ) {
//                    //permission from popup granted
//                    readcontacts()
//                } else {
//                    //permission from popup denied
//                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun readcontacts() {
//        val contentResolver = context!!.contentResolver
//        val cursor =
//            contentResolver.query(
//                ContactsContract.Contacts.CONTENT_URI,
//                null,
//                null,
//                null,
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"
//            )
//        contactlist.clear()
//        if (cursor!!.moveToFirst()) {
//            do {
//                val contactitem = Contact()
//                contactitem.key =
//                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)))
//                contactitem.name =
//                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
//                val image_uri =
//                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
//                if (image_uri != null) {
//                    contactitem.imageurl = image_uri
//                } else {
//                    contactitem.imageurl = Uri.parse(
//                        ContentResolver.SCHEME_ANDROID_RESOURCE +
//                                "://" + resources.getResourcePackageName(R.drawable.avatar2)
//                                + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
//                            R.drawable.avatar2
//                        )
//                    ).toString()
//                }
//                contactlist.add(contactitem)
//            } while (cursor.moveToNext())
//        }
//
//        val rvAdapter = Rv_ContactAdapter(contactlist)
//
//        rvAdapter.mOnItemClickListener = object : OnItemClickListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
//                val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)
//
//                if (countselected.size != 0) {
//                    appbartitle.text = "${countselected.size} selected"
//
//                    if (!toolbarmenuflag) {
//                        toolbar.inflateMenu(R.menu.contacts_menu)
//                        toolbarmenuflag = true
//                    }
//
//                    toolbar.setOnMenuItemClickListener(androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
//                        when (it.itemId) {
//                            R.id.add_guest -> {
////                                val evententity = EventEntity()
////                                evententity.getEventKey(
////                                    eventspinner.selectedItem.toString(),
////                                    object : FirebaseSuccessListenerSingleValue {
////                                        override fun onDatafound(key: String) {
//                                            for (index in countselected) {
//                                                val guest = GuestEntity()
//                                                //guest.eventid = key
//                                                guest.contactid = contactlist[index].key
//                                                guest.addGuest(activity!!.applicationContext)
//                                            }
//                                            rvAdapter.onClearSelected()
//                                     //   }
//                                  //  }
//                                //)
//                                appbartitle.text = "Contacts"
//                                toolbarmenuflag = false
//                                toolbar.menu.clear()
//                                true
//                            }
//                            R.id.add_vendor -> {
//                                for (index in countselected) {
//                                    val vendor = VendorEntity()
//                                    //vendor.eventid = key
//                                    vendor.contactid = contactlist[index].key
//                                    vendor.addVendor()
//                                }
//                                rvAdapter.onClearSelected()
//                                appbartitle.text = "Contacts"
//                                toolbarmenuflag = false
//                                toolbar.menu.clear()
//                                true
//                            }
//
//                        }
//                        false
//                    })
//
//                } else {
//                    appbartitle.text = "Contacts"
//                    toolbarmenuflag = false
//                    toolbar.menu.clear()
//                }
//            }
//        }
//        recyclerViewAllContacts.adapter = rvAdapter
//    }


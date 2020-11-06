package com.example.newevent2

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.contacts.*
import kotlinx.android.synthetic.main.contacts_all.*
import kotlinx.android.synthetic.main.contacts_all.view.*
import java.lang.ClassCastException

class ContactsAll : Fragment() {

    var contactlist = ArrayList<Contact>()
    var eventkey: String? = null
    lateinit var recyclerViewAllContacts: RecyclerView
    var toolbarmenuflag = false
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar =
            activity!!.findViewById(R.id.toolbar)
        val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)
        appbartitle.text = "Contacts"

        val eventspinner = activity!!.findViewById<Spinner>(R.id.eventspinner)
        val evententity = EventEntity()
        evententity.getEventNames(object : FirebaseSuccessListenerList {
            override fun onListCreated(list: ArrayList<Any>) {
                val eventlistadapter = activity?.let {
                    ArrayAdapter(it, R.layout.simple_spinner_item_event, list)
                }
                eventlistadapter!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_event)
                eventspinner.adapter = null
                eventspinner.adapter = eventlistadapter
                Log.i("SpinnerList", list.toString())
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.contacts_all, container, false)

        recyclerViewAllContacts = inf.recyclerViewContacts
        recyclerViewAllContacts.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        //Request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(context!!, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                readcontacts()
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
                    readcontacts()
                } else {
                    //permission from popup denied
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readcontacts() {
        val contentResolver = context!!.contentResolver
        val cursor =
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"
            )
        if (cursor!!.moveToFirst()) {
            do {
                val contactitem = Contact()
                contactitem.key =
                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)))
                contactitem.name =
                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                val image_uri =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                if (image_uri != null) {
                    contactitem.imageurl = image_uri
                } else {
                    contactitem.imageurl = Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE +
                                "://" + resources.getResourcePackageName(R.drawable.avatar2)
                                + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
                            R.drawable.avatar2
                        )
                    ).toString()
                }
                contactlist.add(contactitem)
            } while (cursor.moveToNext())
        }

        val rvAdapter = Rv_ContactAdapter(contactlist)

        rvAdapter.mOnItemClickListener = object : OnItemClickListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
                val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)

                if (countselected.size != 0) {
                    appbartitle.text = "${countselected.size} selected"

                    if (!toolbarmenuflag) {
                        toolbar.inflateMenu(R.menu.contacts_menu)
                        toolbarmenuflag = true
                    }

                    toolbar.setOnMenuItemClickListener(androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.add_guest -> {
                                val evententity = EventEntity()
                                evententity.getEventKey(
                                    eventspinner.selectedItem.toString(),
                                    object : FirebaseSuccessListenerSingleValue {
                                        override fun onDatafound(key: String) {
                                            for (index in countselected) {
                                                val guest = GuestEntity()
                                                guest.eventid = key
                                                guest.contactid = contactlist[index].key
                                                guest.addGuest()
                                            }
                                            rvAdapter.onClearSelected()
                                        }
                                    }
                                )
                                appbartitle.text = "Contacts"
                                toolbarmenuflag = false
                                toolbar.menu.clear()
                                true
                            }
                            R.id.add_vendor -> {
//                                val evententity = EventEntity()
//                                evententity.getEventKey(
//                                    eventspinner.selectedItem.toString(),
//                                    object : FirebaseSuccessListenerSingleValue {
//                                        override fun onDatafound(key: String) {
                                for (index in countselected) {
                                    val vendor = VendorEntity()
                                    //vendor.eventid = key
                                    vendor.contactid = contactlist[index].key
                                    vendor.addVendor()
                                }
                                rvAdapter.onClearSelected()
                                appbartitle.text = "Contacts"
                                toolbarmenuflag = false
                                toolbar.menu.clear()
                                true
                            }

                        }
                        false
                    })

                } else {
                    appbartitle.text = "Contacts"
                    toolbarmenuflag = false
                    toolbar.menu.clear()
                }
//                    })
//                    mClearSelected!!.onClearSelected(index)
//                    appbarmenu.setImageDrawable(menuDrawable)
//                    appbarmenu.isClickable = true

//                    appbarmenu.setOnClickListener {
//                        val toolbar = activity!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//                        toolbar.inflateMenu(R.menu.contacts_menu)
//                    }

            }
        }
        recyclerViewAllContacts.adapter = rvAdapter
    }
}


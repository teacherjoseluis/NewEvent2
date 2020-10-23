package com.example.newevent2

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contacts.floatingActionButton
import kotlinx.android.synthetic.main.contacts_all.*
import kotlinx.android.synthetic.main.contacts_all.view.*

class GuestsAll : Fragment() {

    var contactlist = ArrayList<Guest>()
    lateinit var recyclerViewAllGuests: RecyclerView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //toolbar =
        //    activity!!.findViewById(R.id.toolbar)
        //val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)
        //appbartitle.text = "Guests"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.guests_all, container, false)
        //inf.textView9.text = category

        val eventspinner = inf.findViewById<Spinner>(R.id.eventspinner)
        val evententity = EventEntity()
        evententity.getEventNames(object : FirebaseSuccessListenerList {

            override fun onListCreated(list: ArrayList<String>) {
                val eventlistadapter = activity?.let {
                    ArrayAdapter(it, R.layout.simple_spinner_item_event, list)
                }
                eventlistadapter!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_event)
                eventspinner.adapter = null
                eventspinner.adapter = eventlistadapter
            }

        })

        eventspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val eventname = p0!!.getItemAtPosition(p2).toString()
                val evententity = EventEntity()
                //evententity.getEventKey(eventspinner.selectedItem.toString(),
                evententity.getEventKey(eventname,
                    object : FirebaseSuccessListenerSingleValue {
                        override fun onDatafound(key: String) {
                            readguests(key)

                            activity!!.floatingActionButton.setOnClickListener()
                            {
                                val newguest = Intent(activity, NewGuest::class.java)
                                newguest.putExtra("eventkey", key)
                                startActivity(newguest)
                            }
                        }
                    })
            }
        }

        recyclerViewAllGuests = inf.recyclerView
        recyclerViewAllGuests.apply {
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
                val evententity = EventEntity()
                evententity.getFirstEventKey(
                    object : FirebaseSuccessListenerSingleValue {
                        override fun onDatafound(key: String) {
                            readguests(key)
                        }
                    })
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
                    val evententity = EventEntity()
                    evententity.getFirstEventKey(
                        object : FirebaseSuccessListenerSingleValue {
                            override fun onDatafound(key: String) {
                                readguests(key)
                            }
                        })
                } else {
                    //permission from popup denied
                    //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readguests(eventkey: String) {
        val contentResolver = context!!.contentResolver
//        val cursor =
//            contentResolver.query(
//                ContactsContract.Contacts.CONTENT_URI,
//                null,
//                null,
//                null,
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"
//            )

//        val evententity = EventEntity()
//        evententity.getFirstEventKey(
//            object : FirebaseSuccessListener {
//                override fun onDatafound(key: String) {
        val guestentity = GuestEntity()
        guestentity.eventid = eventkey
        guestentity.getGuestsContacts(object : FirebaseSuccessListenerGuest {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onGuestList(list: ArrayList<Guest>) {
                contactlist.clear()
                //val contentResolver = context!!.contentResolver


                for (guest in list) {

                    val whereclause = StringBuffer()
                    whereclause.append(ContactsContract.Contacts._ID)
                    whereclause.append(" = ")
                    whereclause.append(guest.contactid)

                    var contactname: String? = null
                    var contactphoto: String? = null
                    var cursor: Cursor? = null

                    if (guest.contactid != "local") {
                        cursor =
                            contentResolver.query(
                                ContactsContract.Contacts.CONTENT_URI,
                                null,
                                whereclause.toString(),
                                null, null
                            )

                        cursor?.moveToNext()
                        contactname =
                            cursor?.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY))
                                .toString()
                        contactphoto =
                            cursor?.getString(cursor.getColumnIndex(Contacts.PHOTO_URI)).toString()
                    }

//                    val cursor = contentResolver.query(
//                        Uri.withAppendedPath(
//                            ContactsContract.Contacts.CONTENT_LOOKUP_URI,
//                            "/" + guest.contactid
//                        ), null, null, null, null
//                    )

                    val contactitem = Guest()
                    contactitem.name = contactname?.let { it } ?: guest.name
                    contactitem.contactid = guest.contactid
                    contactitem.rsvp = guest.rsvp
                    contactitem.companion = guest.companion
                    contactitem.table = guest.table
                    contactitem.eventid = guest.eventid
                    val imageuri = contactphoto?.let { it } ?: guest.imageurl
                    if (imageuri.isNotEmpty()) {
                        contactitem.imageurl = imageuri
                    } else {
                        contactitem.imageurl = Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + resources.getResourcePackageName(R.drawable.avatar2)
                                    + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
                                R.drawable.avatar2
                            )
                        ).toString()
                    }
                    contactitem.key = guest.key
                    contactitem.phone = guest.phone
                    contactitem.email = guest.email

                    contactlist.add(contactitem)
                    cursor?.let { cursor.close() }
                }
                val rvAdapter = Rv_GuestAdapter(contactlist)
                recyclerViewAllGuests.adapter = rvAdapter
                val swipeController = SwipeControllerGuest(context!!, rvAdapter, recyclerView)
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
        })
    }

}






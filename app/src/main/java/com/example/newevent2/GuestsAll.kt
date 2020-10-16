package com.example.newevent2

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contacts_all.*
import kotlinx.android.synthetic.main.contacts_all.view.*
import kotlinx.android.synthetic.main.contacts_all.view.eventspinner

class GuestsAll : Fragment() {

    var contactlist = ArrayList<Contact>()
    lateinit var recyclerViewAllGuests: RecyclerView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar =
            activity!!.findViewById(R.id.toolbar)
        val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)
        appbartitle.text = "Guests"
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
        evententity.getEventNames(object : FirebaseSuccessListener {
            override fun onDatafound(key: String) {
                TODO("Not yet implemented")
            }

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
                    object : FirebaseSuccessListener {
                        override fun onDatafound(key: String) {
                            readguests(key)
                        }

                        override fun onListCreated(list: ArrayList<String>) {
                            TODO("Not yet implemented")
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
                    object : FirebaseSuccessListener {
                        override fun onDatafound(key: String) {
                            readguests(key)
                        }

                        override fun onListCreated(list: ArrayList<String>) {
                            TODO("Not yet implemented")
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
                        object : FirebaseSuccessListener {
                            override fun onDatafound(key: String) {
                                readguests(key)
                            }

                            override fun onListCreated(list: ArrayList<String>) {
                                TODO("Not yet implemented")
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
        val cursor =
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"
            )

//        val evententity = EventEntity()
//        evententity.getFirstEventKey(
//            object : FirebaseSuccessListener {
//                override fun onDatafound(key: String) {
        val guestentity = GuestEntity()
        guestentity.eventid = eventkey
        guestentity.getGuestsContacts(object : FirebaseSuccessListener {
            override fun onDatafound(key: String) {
                TODO("Not yet implemented")
            }

            override fun onListCreated(list: ArrayList<String>) {
                contactlist.clear()
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
                        if (list.contains(
                                cursor.getString(
                                    cursor.getColumnIndex(
                                        ContactsContract.Contacts.LOOKUP_KEY
                                    )
                                )
                            )
                        ) {
                            val contactitem = Contact()
                            contactitem.key =
                                (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)))
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
                        }
                    } while (cursor.moveToNext())
                }
                val rvAdapter = Rv_GuestAdapter(contactlist)
                recyclerViewAllGuests.adapter = rvAdapter
            }
        })
    }


}





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
    var mOnFragmentLoadListener: com.example.newevent2.OnCompleteListener? = null

    //var mClearSelected: ClearSelected? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //eventkey = this.arguments!!.get("eventkey").toString()
        toolbar =
            activity!!.findViewById(R.id.toolbar)
        val appbartitle = activity!!.findViewById<TextView>(R.id.appbartitle)
        appbartitle.text = "Contacts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.contacts_all, container, false)
        //inf.textView9.text = category

        val actionbutton = activity!!.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        actionbutton.isVisible = false

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
                Log.i(
                    "SPINNER",
                    "************ Estoy agregando al spinner **************************"
                )
                Log.i("SpinnerList", list.toString())
            }
        })

        recyclerViewAllContacts = inf.recyclerView
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
        //mOnFragmentLoadListener?.onComplete()
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
                //contactitem.imageurl = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)))
                //val image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.PHOTO_URI))
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
//                val appbarmenu = activity!!.findViewById<ImageView>(R.id.appbarmenu)
//                val menuDrawable = ContextCompat.getDrawable(context!!, R.drawable.icons8_menu_vertical_24)!!

                if (countselected.size != 0) {
                    appbartitle.text = "${countselected.size} selected"

                    if (!toolbarmenuflag) {
                        toolbar.inflateMenu(R.menu.contacts_menu)
                        toolbarmenuflag = true
                    }

                    //var myeventkey = ""
                    toolbar.setOnMenuItemClickListener(androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.action_add -> {
                                val evententity = EventEntity()
                                evententity.getEventKey(
                                    eventspinner.selectedItem.toString(),
                                    object : FirebaseSuccessListenerSingleValue {
                                        override fun onDatafound(key: String) {
//                                            //Toast.makeText(activity, key, Toast.LENGTH_SHORT).show()
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
                                //countselected.clear()
                                appbartitle.text = "Contacts"
                                toolbarmenuflag = false
                                toolbar.menu.clear()
                            }
                            R.id.action_settings -> {
                                //Toast.makeText(activity,"Settings ${countselected.size}",Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    })
//                    mClearSelected!!.onClearSelected(index)
//                    appbarmenu.setImageDrawable(menuDrawable)
//                    appbarmenu.isClickable = true

//                    appbarmenu.setOnClickListener {
//                        val toolbar = activity!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//                        toolbar.inflateMenu(R.menu.contacts_menu)
//                    }

                } else {
                    appbartitle.text = "Contacts"
//                    appbarmenu.setImageDrawable(null)
//                    appbarmenu.isClickable = false
                    toolbarmenuflag = false
                    toolbar.menu.clear()
                }
            }
        }
        recyclerViewAllContacts.adapter = rvAdapter
    }
}


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
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.contacts.*
import kotlinx.android.synthetic.main.contacts_all.recyclerViewContacts
import kotlinx.android.synthetic.main.contacts_all.view.*
import kotlinx.android.synthetic.main.guests_all.*
import kotlinx.android.synthetic.main.guests_all.view.*
import java.util.*
import kotlin.collections.ArrayList


class GuestsAll : Fragment() {

    var contactlist = ArrayList<Guest>()
    var eventkey: String? = null
    lateinit var recyclerViewAllGuests: RecyclerView

    lateinit var eventspinner: Spinner
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = activity!!.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)

        eventspinner = activity!!.findViewById<Spinner>(R.id.eventspinner)
        eventspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val eventname = p0!!.getItemAtPosition(p2).toString()
                val evententity = EventEntity()
                evententity.getEventKey(
                    eventname,
                    object : FirebaseSuccessListenerSingleValue {
                        override fun onDatafound(key: String) {
                            readguests(key)

                            activity!!.floatingActionButtonGuest.setOnClickListener()
                            {
                                val newguest = Intent(activity, NewGuest::class.java)
                                newguest.putExtra("eventkey", eventkey)
                                startActivity(newguest)
                            }
                        }
                    })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.guests_menu, menu)

        val sortItem = menu.findItem(R.id.action_sortguest)
        sortItem.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sortguest -> {
                    contactlist.sortWith(Comparator { object1, object2 ->
                        object2.name.compareTo(
                            object1.name
                        )
                    })
                    recyclerViewAllGuests.adapter?.notifyDataSetChanged()
                }
            }
            true
        }

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false

        searchView.setOnSearchClickListener {
            eventspinner.isEnabled = false
        }

        searchView.setOnCloseListener {
            eventspinner.isEnabled = true
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList: List<Guest> = filter(contactlist, p0)
                val rvAdapter = Rv_GuestAdapter(filteredModelList as MutableList<Guest>)
                recyclerViewAllGuests.adapter = rvAdapter
                return true
            }
        })
    }

    private fun filter(models: ArrayList<Guest>, query: String?): List<Guest> {
        val lowerCaseQuery = query!!.toLowerCase()
        val filteredModelList: ArrayList<Guest> = ArrayList()
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
        val inf = inflater.inflate(R.layout.guests_all, container, false)

        recyclerViewAllGuests = inf.recyclerViewGuests
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
        val guestentity = GuestEntity()
        guestentity.eventid = eventkey
        guestentity.getGuestsContacts(object : FirebaseSuccessListenerGuest {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onGuestList(list: ArrayList<Guest>) {
                contactlist.clear()

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
                        contactphoto =
                            cursor?.getString(cursor.getColumnIndex(Contacts.PHOTO_URI))
                    }

                    val contactitem = Guest()
                    contactitem.name = contactname?.let { it } ?: guest.name
                    contactitem.contactid = guest.contactid
                    contactitem.rsvp = when (guest.rsvp) {
                        "y" -> "Yes"
                        "n" -> "No"
                        "pending" -> "Pending"
                        else -> "N/A"
                    }
                    contactitem.companion = guest.companion
                    contactitem.table = guest.table
                    contactitem.eventid = guest.eventid
                    val imageuri = contactphoto?.let { it } ?: guest.imageurl
                    if (imageuri.isNullOrEmpty()) {
                        contactitem.imageurl = Uri.parse(
                            ContentResolver.SCHEME_ANDROID_RESOURCE +
                                    "://" + resources.getResourcePackageName(R.drawable.avatar2)
                                    + '/' + resources.getResourceTypeName(R.drawable.avatar2) + '/' + resources.getResourceEntryName(
                                R.drawable.avatar2
                            )
                        ).toString()
                    } else {
                        contactitem.imageurl = imageuri
                    }
                    contactitem.key = guest.key
                    contactitem.phone = guest.phone
                    contactitem.email = guest.email
                    contactlist.add(contactitem)
                    cursor?.let { cursor.close() }
                }

                val rvAdapter = Rv_GuestAdapter(contactlist)
                recyclerViewAllGuests.adapter = rvAdapter
                val swipeController =
                SwipeControllerTasks(context!!, rvAdapter, recyclerViewAllGuests,null,"delete")
                val itemTouchHelper = ItemTouchHelper(swipeController)
                itemTouchHelper.attachToRecyclerView(recyclerViewGuests)
            }

            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
                TODO("Not yet implemented")
            }
        })
    }
}
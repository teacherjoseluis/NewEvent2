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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tasklist_tasks.view.*

class ContactsAll : Fragment() {

    var contactlist = ArrayList<Contact>()
    lateinit var recyclerViewAllContacts : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.contacts_all, container, false)
        //inf.textView9.text = category

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
            MainActivity.PERMISSION_CODE -> {
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
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY+" DESC")
        if (cursor!!.moveToFirst()) {
            do {
                val contactitem = Contact()
                contactitem.key = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)))
                contactitem.name = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                //contactitem.imageurl = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)))
                //val image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.PHOTO_URI))
                val image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                if (image_uri != null)
                {
                    contactitem.imageurl = image_uri
                }
                else
                {
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
        recyclerViewAllContacts.adapter = rvAdapter
    }

}


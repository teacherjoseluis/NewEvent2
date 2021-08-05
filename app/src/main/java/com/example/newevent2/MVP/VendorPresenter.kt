package com.example.newevent2.MVP

import Application.Cache
import android.Manifest
import android.content.Context;
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.newevent2.Model.*

import com.example.newevent2.VendorsAll

class VendorPresenter : Cache.VendorArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentVA: VendorsAllPresenter
    private lateinit var fragmentCA: ContactsAllPresenter

    private lateinit var cachevendor: Cache<Vendor>

    constructor(context: Context, fragment: VendorsAllPresenter) {
        fragmentVA = fragment
        mContext = context
        activefragment = "VA"
    }

    constructor(context: Context, fragment: ContactsAllPresenter) {
        fragmentCA = fragment
        mContext = context
        activefragment = "CA"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVendorList() {
        cachevendor = Cache(mContext, this)
        cachevendor.loadarraylist(Vendor::class)
    }

    fun getContactsList(){
        var contactlist = ArrayList<Contact>()
        val contentResolver = mContext!!.contentResolver
        val cursor =
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"
            )
        contactlist.clear()
        if (cursor!!.moveToFirst()) {
            do {
                val contactitem = Contact()
                contactitem.key =
                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)))
                contactitem.name =
                    (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                contactlist.add(contactitem)
            } while (cursor.moveToNext())
        }
        fragmentCA.onContactList(contactlist)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onArrayListV(arrayList: ArrayList<Vendor>) {
        if (arrayList.size != 0) {
            when (activefragment) {
                "VA" -> fragmentVA.onVendorList(arrayList)
            }
        }
    }

    override fun onEmptyListV() {
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        val vendor = VendorModel()
        vendor.getAllVendorList(
            user.key,
            user.eventid,
            object : VendorModel.FirebaseSuccessVendorList {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onVendorList(arrayList: ArrayList<Vendor>) {
                    if (arrayList.isNotEmpty()) {
                        // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                        cachevendor.save(arrayList)

                        when (activefragment) {
                            "VA" -> fragmentVA.onVendorList(arrayList)
                        }
                    }else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "VA" -> fragmentVA.onVendorListError(ERRCODEVENDORS) }
                    }
                }
            }
        )
    }


    interface VendorList {
        fun onVendorList(list: ArrayList<Vendor>)
        fun onVendorListError(errcode: String)
    }

    interface ContactList {
        fun onContactList(list: ArrayList<Contact>)
        fun onContactListError(errcode: String)
    }

    companion object {
        const val ERRCODEVENDORS = "NOVENDORS"
        const val ERRCODECONTACTS = "NOCONTACTS"
        const val PERMISSION_CODE = 1001
    }
}

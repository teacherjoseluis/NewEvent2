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
import com.example.newevent2.Model.Contact

import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestModel
import com.example.newevent2.Model.TableGuests
import com.example.newevent2.VendorsAll

class GuestPresenter : Cache.GuestArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentDE: DashboardEventPresenter
    private lateinit var fragmentGA: GuestsAllPresenter
    private lateinit var fragmentCA: ContactsAllPresenter
    private lateinit var fragmentTG: TableGuestsActivityPresenter

    private lateinit var cacheguest: Cache<Guest>
    private lateinit var cachetableguest: Cache<TableGuests>

    constructor(context: Context, fragment: DashboardEventPresenter) {
        fragmentDE = fragment
        mContext = context
        activefragment = "DE"
    }

    constructor(context: Context, fragment: GuestsAllPresenter) {
        fragmentGA = fragment
        mContext = context
        activefragment = "GA"
    }

    constructor(context: Context, fragment: ContactsAllPresenter) {
        fragmentCA = fragment
        mContext = context
        activefragment = "CA"
    }

    constructor(context: Context, fragment: TableGuestsActivityPresenter) {
        fragmentTG = fragment
        mContext = context
        activefragment = "TG"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getGuestList() {
        cacheguest = Cache(mContext, this)
        cacheguest.loadarraylist(Guest::class)
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
    override fun onArrayListG(arrayList: ArrayList<Guest>) {
        if (arrayList.size != 0) {
            when (activefragment) {
                "GA" -> fragmentGA.onGuestList(arrayList)
                "DE" -> fragmentDE.onGuestList(arrayList)
                "TG" -> fragmentTG.onGuestList(arrayList)
            }
        }
    }

    override fun onEmptyListG() {
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        val guest = GuestModel()
        guest.getAllGuestList(
            user.key,
            user.eventid,
            object : GuestModel.FirebaseSuccessGuestList {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onGuestList(arrayList: ArrayList<Guest>) {
                    if (arrayList.isNotEmpty()) {
                        // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                        cacheguest.save(arrayList)

                        when (activefragment) {
                            "GA" -> fragmentGA.onGuestList(arrayList)
                            "DE" -> fragmentDE.onGuestList(arrayList)
                            "TG" -> fragmentTG.onGuestList(arrayList)
                        }
                    }else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "GA" -> fragmentGA.onGuestListError(ERRCODEGUESTS)
                            "DE" -> fragmentDE.onGuestListError(ERRCODEGUESTS)
                            "TG" -> fragmentTG.onGuestListError(ERRCODEGUESTS)
                        }
                    }
                }

            }
        )
    }

    interface GuestList {
        fun onGuestList(list: ArrayList<Guest>)
        fun onGuestListError(errcode: String)
    }

    interface ContactList {
        fun onContactList(list: ArrayList<Contact>)
        fun onContactListError(errcode: String)
    }

    companion object {
        const val ERRCODEGUESTS = "NOGUESTS"
        const val ERRCODECONTACTS = "NOCONTACTS"
        const val PERMISSION_CODE = 1001
    }
}

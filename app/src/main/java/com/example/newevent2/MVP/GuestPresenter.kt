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
import com.example.newevent2.VendorsAll

class GuestPresenter : Cache.GuestArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentDE: DashboardEventPresenter
    private lateinit var fragmentGA: GuestsAllPresenter
    private lateinit var fragmentCA: ContactsAllPresenter

    private lateinit var cacheguest: Cache<Guest>

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
                        }
                    }else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "GA" -> fragmentGA.onGuestListError(ERRCODEGUESTS)
                            "DE" -> fragmentDE.onGuestListError(ERRCODEGUESTS)
                        }
                    }
                }

            }
        )
//        guest.getGuestsEvent(userid, eventid, object : GuestModel.FirebaseSuccessGuestStats {
//            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
//                fragmentMED.onGuestConfirmation(inflatedView, confirmed, rejected, pending)
//            }
//        })
//    }

//    interface GuestStats{
//        fun onGuestConfirmation(inflatedView: View, confirmed: Int, rejected: Int, pending: Int)
//    }
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

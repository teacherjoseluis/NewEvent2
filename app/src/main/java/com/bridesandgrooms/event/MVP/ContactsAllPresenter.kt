package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.UI.Fragments.ContactsAll
import com.bridesandgrooms.event.Model.Contact


class ContactsAllPresenter(
    val context: Context,
    val fragment: ContactsAll
) :
    GuestPresenter.ContactList {

    private var presenterguest: GuestPresenter = GuestPresenter(context, this)
    private val mHandler = Handler(Looper.getMainLooper())

    fun getContactsList() {
        Thread {
            presenterguest.getContactsList()
        }.start()
    }

    override fun onContactList(list: ArrayList<Contact>) {
        mHandler.post {
            fragment.onGAContacts(list)
        }
    }

    override fun onContactListError(errcode: String) {
        mHandler.post {
            fragment.onGAContactsError(GuestPresenter.ERRCODECONTACTS)
        }
    }

    interface GAContacts {
        fun onGAContacts(
            list: ArrayList<Contact>
        )

        fun onGAContactsError(errcode: String)
    }


}

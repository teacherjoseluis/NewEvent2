package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.ContactsAll
import com.example.newevent2.GuestsAll
import com.example.newevent2.Model.Contact
import com.example.newevent2.Model.Guest


    class ContactsAllPresenter(
        val context: Context,
        val fragment: ContactsAll
    ) :
        GuestPresenter.ContactList {

        private var presenterguest: GuestPresenter = GuestPresenter(context!!, this)

        init {
            presenterguest.getContactsList()
        }

        override fun onContactList(list: ArrayList<Contact>) {
            fragment.onGAContacts(list)
        }

        override fun onContactListError(errcode: String) {
            fragment.onGAContactsError(GuestPresenter.ERRCODECONTACTS)
        }

        interface GAContacts {
            fun onGAContacts(
                list: ArrayList<Contact>
            )

            fun onGAContactsError(errcode: String)
        }


    }

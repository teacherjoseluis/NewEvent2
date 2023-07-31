package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.ContactsAll
import com.bridesandgrooms.event.Model.Contact


class ContactsAllPresenter(
        val context: Context,
        val fragment: ContactsAll
    ) :
        GuestPresenter.ContactList {

        private var presenterguest: GuestPresenter = GuestPresenter(context, this)

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

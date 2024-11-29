package com.bridesandgrooms.event.Model

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log

open class Guest(
    var key: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var rsvp: String = "pending",
    var companion: String = "none",
    var table: String = "none",
    var createdatetime: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        key = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        email = parcel.readString().toString()
        rsvp = parcel.readString().toString()
        companion = parcel.readString().toString()
        table = parcel.readString().toString()
        createdatetime = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(rsvp)
        parcel.writeString(companion)
        parcel.writeString(table)
        parcel.writeString(createdatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * This function implements a method in GuestDBHelper that gets the total count of Guests invited to the Event
     */
    fun getGuestCount(context: Context): Int? {
        try {
            val guestDBHelper = GuestDBHelper(context)
            return guestDBHelper.getNumberGuests()!!
        } catch (e: Exception) {
            Log.e(GuestDBHelper.TAG, e.message.toString())
            return null
        }
    }

    /**
     * This function converts a Contact into a Guest, getting his phone and email if existent
     */
    internal fun contacttoGuest(context: Context, contactid: String): Guest {
        val contactguest = Guest()
        val cursor: Cursor?
        val phonecursor: Cursor?
        val emailcursor: Cursor?

        val whereclause = StringBuffer()
        whereclause.append(ContactsContract.Contacts._ID)
        whereclause.append(" = ")
        whereclause.append(contactid)

        val whereclausephone = StringBuffer()
        whereclausephone.append(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        whereclausephone.append(" = ")
        whereclausephone.append(contactid)

        val whereclauseemail = StringBuffer()
        whereclauseemail.append(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
        whereclauseemail.append(" = ")
        whereclauseemail.append(contactid)

        cursor =
            context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                whereclause.toString(),
                null, null
            )

        phonecursor =
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                whereclausephone.toString(),
                null, null
            )

        emailcursor =
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                whereclauseemail.toString(),
                null, null
            )

        cursor?.moveToNext()
        phonecursor?.moveToNext()
        emailcursor?.moveToNext()

        contactguest.name =
            cursor?.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                .toString()

        contactguest.phone =
            try {
                phonecursor?.getString(phonecursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .toString()
            } catch (e: IndexOutOfBoundsException) {
                ""
            }
        contactguest.email =
            try {
                emailcursor?.getString(emailcursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
                    .toString()
            } catch (e: IndexOutOfBoundsException) {
                ""
            }

        cursor?.let { cursor.close() }
        phonecursor?.let { phonecursor.close() }
        emailcursor?.let { emailcursor.close() }

        return contactguest
    }

    companion object CREATOR : Parcelable.Creator<Guest> {
        override fun createFromParcel(parcel: Parcel): Guest {
            return Guest(parcel)
        }

        override fun newArray(size: Int): Array<Guest?> {
            return arrayOfNulls(size)
        }
    }
}
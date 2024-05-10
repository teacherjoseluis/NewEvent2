package com.bridesandgrooms.event.Model

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract

data class Vendor(
    var key: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var category: String = "",
    var eventid: String = "",
    var placeid: String = "",
    var location: String = "",
    //var createdatetime: String = "",
    var googlevendorname: String = "",
    var ratingnumber: Float = 0F,
    var reviews: Float = 0F,
    var rating: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        key = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        email = parcel.readString().toString()
        category = parcel.readString().toString()
        eventid = parcel.readString().toString()
        placeid = parcel.readString().toString()
        location = parcel.readString().toString()
        //createdatetime = parcel.readString().toString()
        googlevendorname = parcel.readString().toString()
        ratingnumber = parcel.readFloat()
        reviews = parcel.readFloat()
        rating = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(category)
        parcel.writeString(eventid)
        parcel.writeString(placeid)
        parcel.writeString(location)
        //parcel.writeString(createdatetime)
        parcel.writeString(googlevendorname)
        parcel.writeFloat(ratingnumber)
        parcel.writeFloat(reviews)
        parcel.writeString(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vendor> {
        override fun createFromParcel(parcel: Parcel): Vendor {
            return Vendor(parcel)
        }

        override fun newArray(size: Int): Array<Vendor?> {
            return arrayOfNulls(size)
        }
    }

    internal fun contacttoVendor(context: Context, contactid: String): Vendor {
        val contactvendor = Vendor()
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

        contactvendor.name =
            cursor?.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                .toString()

        contactvendor.phone =
            try {
                phonecursor?.getString(phonecursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .toString()
            } catch (e: IndexOutOfBoundsException) {
                ""
            }
        contactvendor.email =
            try {
                emailcursor?.getString(emailcursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
                    .toString()
            } catch (e: IndexOutOfBoundsException) {
                ""
            }

        cursor?.let { cursor.close() }
        phonecursor?.let { phonecursor.close() }
        emailcursor?.let { emailcursor.close() }

//    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//    mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
//        PhoneNumberUtils.formatNumber(
//            mPhoneNumber.text.toString(),
//            tm.simCountryIso
//        )
//    }
        return contactvendor
    }
}
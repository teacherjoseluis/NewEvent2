package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable

open class Vendor(
    var key: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var category: String = "",
    var eventid: String = "",
    var placeid: String = "",
    var location: String = "",
    var createdatetime: String = ""
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
        createdatetime = parcel.readString().toString()
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
        parcel.writeString(createdatetime)
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
}
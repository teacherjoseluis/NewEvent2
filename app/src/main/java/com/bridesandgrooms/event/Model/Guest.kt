package com.bridesandgrooms.event.Model

import android.os.Parcel
import android.os.Parcelable

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

    companion object CREATOR : Parcelable.Creator<Guest> {
        override fun createFromParcel(parcel: Parcel): Guest {
            return Guest(parcel)
        }

        override fun newArray(size: Int): Array<Guest?> {
            return arrayOfNulls(size)
        }
    }
}
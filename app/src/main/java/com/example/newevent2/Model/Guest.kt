package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable

open class Guest(
    var eventid: String = "",
    var contactid: String = "",
    var rsvp: String = "",
    var companion: String = "",
    var table: String = ""
) : Contact(), Parcelable {

    constructor(parcel: Parcel) : this() {
        parcel.readString().toString()
        parcel.readString().toString()
        parcel.readString().toString()
        parcel.readString().toString()
        parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventid)
        parcel.writeString(contactid)
        parcel.writeString(rsvp)
        parcel.writeString(companion)
        parcel.writeString(table)
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
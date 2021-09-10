package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable

open class Payment(
    var key: String = "",
    var name: String = "",
    var date: String = "",
    var category: String = "",
    var amount: String = "",
    var eventid: String = "",
    var createdatetime: String = "",
    var vendorid: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        key = parcel.readString().toString()
        name = parcel.readString().toString()
        date = parcel.readString().toString()
        category = parcel.readString().toString()
        amount = parcel.readString().toString()
        eventid = parcel.readString().toString()
        createdatetime = parcel.readString().toString()
        vendorid = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(category)
        parcel.writeString(amount)
        parcel.writeString(eventid)
        parcel.writeString(createdatetime)
        parcel.writeString(vendorid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }
}
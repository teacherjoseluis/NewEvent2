package com.bridesandgrooms.event.Model

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
}
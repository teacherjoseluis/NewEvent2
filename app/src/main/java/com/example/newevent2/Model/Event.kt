package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable

open class Event() : Parcelable {

    var key: String = ""
    var imageurl: String = ""
    var placeid: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var address: String = ""
    var name: String = ""
    var date: String = ""
    var time: String = ""
    var about: String = ""
    var location: String = ""

    constructor(parcel: Parcel) : this() {
        key = parcel.readString().toString()
        imageurl = parcel.readString().toString()
        placeid = parcel.readString().toString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        address = parcel.readString().toString()
        name = parcel.readString().toString()
        date = parcel.readString().toString()
        time = parcel.readString().toString()
        about = parcel.readString().toString()
        location = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(imageurl)
        parcel.writeString(placeid)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(about)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
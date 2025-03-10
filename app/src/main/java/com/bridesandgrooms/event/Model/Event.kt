package com.bridesandgrooms.event.Model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.text.DecimalFormat

open class Event(
    var key: String = "",
    var imageurl: String = "",
    var placeid: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = "",
    var name: String = "",
    var date: String = "",
    var time: String = "",
    var eventid: String = "",
    var location: String = ""
) : Parcelable {

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
        eventid = parcel.readString().toString()
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
        parcel.writeString(eventid)
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
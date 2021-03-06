package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable

open class Task(
    var key: String = "",
    var name: String = "",
    var date: String = "",
    var category: String = "",
    var budget: String = "",
    var status: String = "",
    var eventid: String = "",
    var createdatetime: String = "") : Parcelable {

    constructor(parcel: Parcel) : this() {
        key = parcel.readString().toString()
        name = parcel.readString().toString()
        date = parcel.readString().toString()
        category = parcel.readString().toString()
        budget = parcel.readString().toString()
        status = parcel.readString().toString()
        eventid = parcel.readString().toString()
        createdatetime = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(category)
        parcel.writeString(budget)
        parcel.writeString(status)
        parcel.writeString(eventid)
        parcel.writeString(createdatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
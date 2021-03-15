package com.example.newevent2

import android.os.Parcel
import android.os.Parcelable

open class User() :Parcelable {

    var key: String = ""
    var eventid: String = ""
    var shortname: String = ""
    var email: String = ""
    var country: String = ""
    var language: String = ""
    var createdatetime: String = ""
    var authtype: String = ""
    var status: String = ""
    var imageurl: String = ""
    var role: String = ""
    var hasevent: String = ""
    var hastask: String = ""
    var haspayment: String = ""
    var hasguest: String = ""
    var hasvendor: String = ""

    constructor(parcel: Parcel) : this() {
        key = parcel.readString()!!
        eventid = parcel.readString()!!
        shortname = parcel.readString()!!
        email = parcel.readString()!!
        country = parcel.readString()!!
        language = parcel.readString()!!
        createdatetime = parcel.readString()!!
        authtype = parcel.readString()!!
        status = parcel.readString()!!
        imageurl = parcel.readString()!!
        role = parcel.readString()!!
        hasevent = parcel.readString()!!
        hastask = parcel.readString()!!
        haspayment = parcel.readString()!!
        hasguest = parcel.readString()!!
        hasvendor = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(eventid)
        parcel.writeString(shortname)
        parcel.writeString(email)
        parcel.writeString(country)
        parcel.writeString(language)
        parcel.writeString(createdatetime)
        parcel.writeString(authtype)
        parcel.writeString(status)
        parcel.writeString(imageurl)
        parcel.writeString(role)
        parcel.writeString(hasevent)
        parcel.writeString(hastask)
        parcel.writeString(haspayment)
        parcel.writeString(hasguest)
        parcel.writeString(hasvendor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
package com.example.newevent2

import android.os.Parcel
import android.os.Parcelable

class UserSession(
    var uid: String="",
    var eventid: String="",
    var shortname: String="",
    var useremail: String="",
    var authtype: String="",
    var country: String="",
    var language: String="",
    var createddate: String="",
    var status: String="",
    var hasevent: String = "",
    var hastask: String = "",
    var haspayment: String = "",
    var hasguest: String = "",
    var hasvendor: String = ""
//need to add the flags to calculate the progress made on the user account
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(eventid)
        parcel.writeString(shortname)
        parcel.writeString(useremail)
        parcel.writeString(authtype)
        parcel.writeString(country)
        parcel.writeString(language)
        parcel.writeString(createddate)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserSession> {
        override fun createFromParcel(parcel: Parcel): UserSession {
            return UserSession(parcel)
        }

        override fun newArray(size: Int): Array<UserSession?> {
            return arrayOfNulls(size)
        }
    }
}
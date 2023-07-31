package com.bridesandgrooms.event.Model

import android.os.Parcel
import android.os.Parcelable

open class Note(
    var noteid: String = "",
    var title: String = "",
    var body: String = "",
    var color: String = "",
    var lastupdateddatetime: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(noteid)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeString(color)
        parcel.writeString(lastupdateddatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}
package com.bridesandgrooms.event.Model.DashboardImage

import android.os.Parcel
import android.os.Parcelable

data class DashboardImageData(
    var key: String = "",
    var code: String = "",
    var photographer: String = "",
    var regularImageUrl: String = "",
    var thumbImageUrl: String = "",
    var nameEn: String = "",
    var nameEs: String = "",
    var descriptionEn: String = "",
    var descriptionEs: String = ""
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
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(code)
        parcel.writeString(photographer)
        parcel.writeString(regularImageUrl)
        parcel.writeString(thumbImageUrl)
        parcel.writeString(nameEn)
        parcel.writeString(nameEs)
        parcel.writeString(descriptionEn)
        parcel.writeString(descriptionEs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DashboardImageData> {
        override fun createFromParcel(parcel: Parcel): DashboardImageData {
            return DashboardImageData(parcel)
        }

        override fun newArray(size: Int): Array<DashboardImageData?> {
            return arrayOfNulls(size)
        }
    }
}

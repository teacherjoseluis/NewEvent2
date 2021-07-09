package com.example.newevent2.Model

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.ArrayList

data class TableGuests(val table: String, val count: Int, val tableguestlist: ArrayList<Guest>) {

}
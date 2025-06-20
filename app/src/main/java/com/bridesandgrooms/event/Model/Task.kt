package com.bridesandgrooms.event.Model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.bridesandgrooms.event.Functions.getlocale

open class Task(
    var key: String = "",
    var name: String = "",
    var date: String = "",
    var category: String = "",
    var budget: String = "",
    var status: String = "",
    var eventid: String = "",
    var createdatetime: String = ""
) : Parcelable {

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

    // Secondary constructor for generating a task with dummy values
    constructor(dummy: Boolean) : this(
        key = if (dummy) "" else "",
        name = if (dummy) "Sample Task" else "",
        date = if (dummy) "31/12/2023" else "",
        category = if (dummy) "ceremony" else "",
        budget = if (dummy) "100.00" else ""
    )

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

        fun getTaskStatusName(code: String): String {
            if (getlocale().substring(0, 2) == "es") {
                return when (code) {
                    "A" -> "Por Completar"
                    "C" -> "Completada"
                    else -> "Por Completar"
                }
            } else {
                return when (code) {
                    "A" -> "To Be Completed"
                    "C" -> "Completed"
                    else -> "To Be Completed"
                }
            }
        }
    }
}

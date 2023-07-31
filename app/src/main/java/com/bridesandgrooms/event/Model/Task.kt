package com.bridesandgrooms.event.Model

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

	// Secondary constructor for generating a task with dummy values
	constructor(dummy: Boolean) : this(
		key = if (dummy) "dummy_key" else "",
		name = if (dummy) "Sample Task" else "",
		date = if (dummy) "2023-12-31" else "",
		category = if (dummy) "Sample Category" else "",
		budget = if (dummy) "Sample Budget" else "",
		status = if (dummy) "Sample Status" else "",
		eventid = if (dummy) "dummy_event_id" else "",
		createdatetime = if (dummy) "2023-12-31T12:00:00" else ""
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
	}
}
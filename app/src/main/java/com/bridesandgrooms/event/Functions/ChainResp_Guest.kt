package com.bridesandgrooms.event.Functions

import android.content.Context
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User

interface CoRAddEditGuest {
    fun onAddEditGuest(guest: Guest)
}

interface CoRDeleteGuest {
    fun onDeleteGuest(guestId: String)
}
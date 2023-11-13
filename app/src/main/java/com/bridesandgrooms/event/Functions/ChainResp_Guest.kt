package com.bridesandgrooms.event.Functions

import com.bridesandgrooms.event.Model.Guest

interface CoRAddEditGuest {
    fun onAddEditGuest(guest: Guest)
}

interface CoRDeleteGuest {
    fun onDeleteGuest(guest: Guest)
}
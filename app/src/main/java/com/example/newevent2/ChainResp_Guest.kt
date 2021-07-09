package com.example.newevent2

import com.example.newevent2.Model.Guest

interface CoRAddEditGuest {
    fun onAddEditGuest(guest: Guest)
}

interface CoRDeleteGuest {
    fun onDeleteGuest(guest: Guest)
}
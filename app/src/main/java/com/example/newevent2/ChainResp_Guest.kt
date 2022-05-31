package com.example.newevent2

import com.example.newevent2.Model.Guest

interface CoRAddEditGuest {
    suspend fun onAddEditGuest(guest: Guest)
}

interface CoRDeleteGuest {
    fun onDeleteGuest(guest: Guest)
}
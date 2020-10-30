package com.example.newevent2

interface FirebaseSuccessListenerGuest {
    fun onGuestList(list: ArrayList<Guest>)
    fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int)
}
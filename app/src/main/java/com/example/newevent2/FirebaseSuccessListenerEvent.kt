package com.example.newevent2

interface FirebaseSuccessListenerEvent {
    fun onEventList(list: ArrayList<Event>)
    fun onEvent(event: Event)
}
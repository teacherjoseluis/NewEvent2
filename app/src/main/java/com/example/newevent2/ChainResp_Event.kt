package com.example.newevent2

import com.example.newevent2.Model.Event

interface CoRAddEditEvent {
    fun onAddEditEvent(event: Event)
}
package com.example.newevent2

import com.example.newevent2.Model.Event

interface CoRAddEditEvent {
    suspend fun onAddEditEvent(event: Event)
}
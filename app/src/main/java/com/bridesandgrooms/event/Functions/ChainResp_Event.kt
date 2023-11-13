package com.bridesandgrooms.event.Functions

import com.bridesandgrooms.event.Model.Event

interface CoRAddEditEvent {
    suspend fun onAddEditEvent(event: Event)
}
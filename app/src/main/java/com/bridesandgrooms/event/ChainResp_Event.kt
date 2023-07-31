package com.bridesandgrooms.event

import com.bridesandgrooms.event.Model.Event

interface CoRAddEditEvent {
    suspend fun onAddEditEvent(event: Event)
}
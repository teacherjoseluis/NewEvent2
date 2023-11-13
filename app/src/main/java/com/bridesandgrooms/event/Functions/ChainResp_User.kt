package com.bridesandgrooms.event.Functions

import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.User


interface CoRAddEditUser {
    suspend fun onAddEditUser(user: User)
}

interface CoROnboardUser {
    suspend fun onOnboardUser(user: User, event: Event)
}
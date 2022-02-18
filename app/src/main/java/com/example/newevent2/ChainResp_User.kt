package com.example.newevent2

import com.example.newevent2.Model.User


interface CoRAddEditUser {
    suspend fun onAddEditUser(user: User)
}
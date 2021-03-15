package com.example.newevent2

import com.example.newevent2.Model.User

interface FirebaseSuccessListenerUser {
    //fun onUserexists(userexists: Boolean)
    fun onUserexists(user: User)
}
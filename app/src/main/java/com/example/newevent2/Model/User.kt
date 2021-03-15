package com.example.newevent2.Model

import com.example.newevent2.FirebaseSuccessListenerUser
import com.example.newevent2.User

open class User(
    var key: String?,
    var eventid: String = "",
    var shortname: String = "",
    var email: String = "",
    var country: String = "",
    var language: String = "",
    var createdatetime: String = "",
    var authtype: String = "",
    var status: String = "",
    var imageurl: String = "",
    var role: String = "",
    var hasevent: String = "",
    var hastask: String = "",
    var haspayment: String = "",
    var hasguest: String = "",
    var hasvendor: String = ""
)
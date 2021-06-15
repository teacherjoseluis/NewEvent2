package com.example.newevent2.Model

open class Guest: Contact() {
    var eventid: String = ""
    var contactid: String = ""
    var rsvp: String = ""
    var companion: String = ""
    var table: String = ""
}
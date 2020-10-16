package com.example.newevent2

interface FirebaseSuccessListener {

    fun onDatafound(key: String)
    fun onListCreated(list: ArrayList<String>)

}
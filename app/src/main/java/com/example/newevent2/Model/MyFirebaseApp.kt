package com.example.newevent2.Model

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MyFirebaseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        /* Enable disk persistence  */
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
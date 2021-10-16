package com.example.newevent2.Model

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase

class MyFirebaseApp: Application() {

    //var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate() {
        super.onCreate()
        /* Enable disk persistence  */
        FirebaseApp.initializeApp(this)
        MyFirebaseApp.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        MobileAds.initialize(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

//    fun getmFirebaseAnalytics(): FirebaseAnalytics? {
//        return mFirebaseAnalytics
//    }

    companion object {
        lateinit var mFirebaseAnalytics : FirebaseAnalytics
        //lateinit var mMobileAds: MobileAds
    }
}
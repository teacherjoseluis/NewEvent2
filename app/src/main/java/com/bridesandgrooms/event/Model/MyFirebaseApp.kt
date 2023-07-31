package com.bridesandgrooms.event.Model

import android.app.Application
import android.util.Log
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.RemoteConfigSingleton.set_category_layout
import com.bridesandgrooms.event.RemoteConfigSingleton.set_enable_foryoutab
import com.bridesandgrooms.event.RemoteConfigSingleton.setautocreateTaskPayment
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class MyFirebaseApp: Application() {

    //var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate() {
        super.onCreate()
        /* Enable disk persistence  */
        FirebaseApp.initializeApp(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        MobileAds.initialize(this)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // --- Remote configuration ---//
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults) // Set default values

// Set cache expiration time (optional)

// Set cache expiration time (optional)
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // 1 hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

// Fetch the remote configurations

// Fetch the remote configurations
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task: Task<Boolean?> ->
                if (task.isSuccessful) {
                    // Configurations successfully fetched and activated
                    // Apply the updated configurations in your app
                    //applyRemoteConfigurations()
                    val remoteConfig = FirebaseRemoteConfig.getInstance()
                    val autocreateTaskPaymentFeature = remoteConfig.getBoolean("auto_create_tasks_and_payments")
                    val enable_foryoutab = remoteConfig.getBoolean("enable_foryoutab")
                    val category_layout = remoteConfig.getString("category_layout")
                    //val themeId = remoteConfig.getString("themeOverride")

                    // Set the chosen theme to your activity
                    set_category_layout(category_layout)
                    set_enable_foryoutab(enable_foryoutab)
                    setautocreateTaskPayment(autocreateTaskPaymentFeature)
                } else {
                    // Error fetching configurations, use default values
                    // Handle the error case
                }
            }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    // Handle the token, e.g., send it to your server or store it locally
                    // You can also use the token to send notifications to this device
                    // For simplicity, we are just logging the token here
                    Log.d(TAG, "FCM Token: $token")
                } else {
                    Log.e(TAG, "Failed to get FCM token")
                }
            }
    }

    private fun applyRemoteConfigurations() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val autocreateTaskPaymentFeature = remoteConfig.getBoolean("auto_create_tasks_and_payments")
        val enable_foryoutab = remoteConfig.getBoolean("enable_foryoutab")
        //val themeId = remoteConfig.getString("themeOverride")

        // Set the chosen theme to your activity
        set_enable_foryoutab(enable_foryoutab)
        setautocreateTaskPayment(autocreateTaskPaymentFeature)
    }

//    fun getmFirebaseAnalytics(): FirebaseAnalytics? {
//        return mFirebaseAnalytics
//    }

    companion object {
        lateinit var mFirebaseAnalytics : FirebaseAnalytics
        //lateinit var mMobileAds: MobileAds
        private const val TAG = "MainActivity"
    }
}
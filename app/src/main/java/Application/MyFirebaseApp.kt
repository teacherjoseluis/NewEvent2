package Application

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_category_layout
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_developer_mail
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_enable_foryoutab
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_reviewbox
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_showads
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_video_login
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.setautocreateTaskPayment
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.NotificationReceiver
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class MyFirebaseApp : Application() {

    private val receiver = NotificationReceiver()

    override fun onCreate() {
        super.onCreate()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // 1 hour
            .build()

        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
                .addOnCompleteListener { task: Task<Boolean?> ->
                    if (task.isSuccessful) {
                        // Configurations successfully fetched and activated
                        // Apply the updated configurations in your app
                        //applyRemoteConfigurations()
                        val remoteConfig = FirebaseRemoteConfig.getInstance()
                        val autocreateTaskPaymentFeature =
                            remoteConfig.getBoolean("auto_create_tasks_and_payments")
                        val enable_foryoutab = remoteConfig.getBoolean("enable_foryoutab")
                        val category_layout = remoteConfig.getString("category_layout")
                        val developer_mail = remoteConfig.getBoolean("developer_mail")
                        val video_login = remoteConfig.getBoolean("video_login")
                        val showads = remoteConfig.getBoolean("showads")
                        val reviewbox = remoteConfig.getBoolean("reviewbox")
                        //val themeId = remoteConfig.getString("themeOverride")

                        // Set the chosen theme to your activity
                        set_category_layout(category_layout)
                        set_enable_foryoutab(enable_foryoutab)
                        setautocreateTaskPayment(autocreateTaskPaymentFeature)
                        set_developer_mail(developer_mail)
                        set_video_login(video_login)
                        set_showads(showads)
                        set_reviewbox(reviewbox)
                    } else {
                        // Error fetching configurations, use default values
                        // Handle the error case
                    }
                }
        }

        FirebaseMessaging.getInstance().apply {
            token
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Register Broadcast Receiver
        val intent = IntentFilter("android.intent.action.DATE_CHANGED")
        applicationContext.registerReceiver(receiver, intent)
        Log.d(TAG, "Register Broadcast receiver")


        /* Enable disk persistence  */
        FirebaseApp.initializeApp(this)
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        MobileAds.initialize(this)

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // --- Remote configuration ---//
        //val remoteConfig = FirebaseRemoteConfig.getInstance()
        //remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults) // Set default values

// Set cache expiration time (optional)

// Set cache expiration time (optional)
//        val configSettings = FirebaseRemoteConfigSettings.Builder()
//            .setMinimumFetchIntervalInSeconds(0) // 1 hour
//            .build()
//        remoteConfig.setConfigSettingsAsync(configSettings)

// Fetch the remote configurations

// Fetch the remote configurations
//        remoteConfig.fetchAndActivate()
//            .addOnCompleteListener { task: Task<Boolean?> ->
//                if (task.isSuccessful) {
//                    // Configurations successfully fetched and activated
//                    // Apply the updated configurations in your app
//                    //applyRemoteConfigurations()
//                    val remoteConfig = FirebaseRemoteConfig.getInstance()
//                    val autocreateTaskPaymentFeature =
//                        remoteConfig.getBoolean("auto_create_tasks_and_payments")
//                    val enable_foryoutab = remoteConfig.getBoolean("enable_foryoutab")
//                    val category_layout = remoteConfig.getString("category_layout")
//                    val developer_mail = remoteConfig.getBoolean("developer_mail")
//                    val video_login = remoteConfig.getBoolean("video_login")
//                    val showads = remoteConfig.getBoolean("showads")
//                    val reviewbox = remoteConfig.getBoolean("reviewbox")
//                    //val themeId = remoteConfig.getString("themeOverride")
//
//                    // Set the chosen theme to your activity
//                    set_category_layout(category_layout)
//                    set_enable_foryoutab(enable_foryoutab)
//                    setautocreateTaskPayment(autocreateTaskPaymentFeature)
//                    set_developer_mail(developer_mail)
//                    set_video_login(video_login)
//                    set_showads(showads)
//                    set_reviewbox(reviewbox)
//                } else {
//                    // Error fetching configurations, use default values
//                    // Handle the error case
//                }
//            }

//        FirebaseMessaging.getInstance().token
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val token = task.result
//                    // Handle the token, e.g., send it to your server or store it locally
//                    // You can also use the token to send notifications to this device
//                    // For simplicity, we are just logging the token here
//                    Log.d(TAG, "FCM Token: $token")
//                } else {
//                    Log.e(TAG, "Failed to get FCM token")
//                }
//            }

    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)

    }

    companion object {
        lateinit var mFirebaseAnalytics: FirebaseAnalytics
        //lateinit var mMobileAds: MobileAds
        private const val TAG = "MyFirebaseApp"
    }
}
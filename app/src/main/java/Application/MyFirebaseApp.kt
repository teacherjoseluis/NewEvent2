package Application

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_category_layout
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_developer_mail
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_enable_foryoutab
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_reviewbox
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_showads
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.set_video_login
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton.setautocreateTaskPayment
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.bridesandgrooms.event.Functions.getUserCountry
import com.bridesandgrooms.event.Model.DatabaseHelper
import com.bridesandgrooms.event.Model.EventDBHelper
import com.bridesandgrooms.event.Model.GuestDBHelper
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.Model.VendorDBHelper
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.NotificationReceiver
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.Locale

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

        appContext = applicationContext
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        AnalyticsManager.getInstance().trackDeviceInfo(this)

        // Register Broadcast Receiver
        val intent = IntentFilter("android.intent.action.DATE_CHANGED")
        applicationContext.registerReceiver(receiver, intent)
        Log.d(TAG, "Register Broadcast receiver")


        /* Enable disk persistence  */
        FirebaseApp.initializeApp(this)
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        MobileAds.initialize(this)

        // Detect proper locale based on user’s environment
        val language = resources.configuration.locales[0].language
        val country = getUserCountry(this)
        val locale = Locale(language, country)

        // Initialize Places with localized context
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key), locale)
        }

        // Start shared Preferences
        UserSessionHelper.initialize(this)
        DatabaseHelper.initialize(this)
        CalendarEvent.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)

    }

    companion object {
        lateinit var mFirebaseAnalytics: FirebaseAnalytics
        lateinit var appContext: Context
        //lateinit var mMobileAds: MobileAds
        private const val TAG = "MyFirebaseApp"
    }
}
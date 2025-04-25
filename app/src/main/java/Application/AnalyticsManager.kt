package Application

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class AnalyticsManager {

    companion object {
        // Singleton instance
        private var instance: AnalyticsManager? = null

        fun getInstance(): AnalyticsManager {
            if (instance == null) {
                instance = AnalyticsManager()
            }
            return instance!!
        }
    }

    fun initialize(context: Context, userId: String?) {
        trackDeviceInfo(context)
        setUserProperties(userId, null, null, null, null, null)
    }

    fun trackDeviceInfo(context: Context) {
        val bundle = Bundle().apply {
            putString("manufacturer", Build.MANUFACTURER)
            putString("model", Build.MODEL)
            putString("os_version", Build.VERSION.RELEASE)
            putInt("sdk_int", Build.VERSION.SDK_INT)

            val locale = Locale.getDefault()
            putString("language", locale.language)
            putString("country", locale.country)

            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            putString("app_version", packageInfo.versionName)
            putLong("app_version_code", packageInfo.longVersionCode)
        }
        MyFirebaseApp.mFirebaseAnalytics.logEvent("DEVICE_INFO", bundle)
    }

    fun setUserProperties(userId: String?, role: String?, numberguests: Int?, eventbudget: String?, gender: String?, agerange: String?) {
        val analytics = FirebaseAnalytics.getInstance(MyFirebaseApp.appContext)

        userId?.let { analytics.setUserId(it) }
        //userType?.let { analytics.setUserProperty("user_type", it) }

        val language = Locale.getDefault().language
        val region = Locale.getDefault().country
        analytics.setUserProperty("language", language)
        analytics.setUserProperty("region", region)
        analytics.setUserProperty("premium_user", "false")
        analytics.setUserProperty("role", role)
        analytics.setUserProperty("guests", numberguests.toString())
        analytics.setUserProperty("budget", eventbudget)
        analytics.setUserProperty("gender", gender)
        analytics.setUserProperty("agerange", agerange)

        val provider = FirebaseAuth.getInstance().currentUser?.providerData?.lastOrNull()?.providerId // e.g., "google.com", "password", etc.
        provider?.let {
            val method = when (it) {
                "google.com" -> "google"
                "facebook.com" -> "facebook"
                "password" -> "email"
                else -> it
            }
            analytics.setUserProperty("registration_method", method)
        }
    }

    // Method for handling user interactions
    fun trackUserInteraction(screenName: String, content: String, action: String?) {
        val bundle = Bundle()
        bundle.putString("screen_name", screenName)
        bundle.putString("content", content)
        bundle.putString("action", action)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("USER_INTERACTION", bundle)

    }

    // Method for handling content interactions
    fun trackContentInteraction(contentId: String, action: String) {
        val bundle = Bundle()
        bundle.putString("content_id", contentId)
        bundle.putString("action", action)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("CONTENT_INTERACTION", bundle)
    }

    // Method for handling settings changes
    fun trackSettingsChange(settingName: String, newValue: String) {
        val bundle = Bundle()
        bundle.putString("setting_name", settingName)
        bundle.putString("new_value", newValue)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("SETTINGS_CHANGE", bundle)
    }

    // Method for handling navigation events
    fun trackNavigationEvent(screenName: String, action: String) {
        val bundle = Bundle()
        bundle.putString("screen_name", screenName)
        bundle.putString("action", action)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("NAVIGATION_EVENT", bundle)
    }

    fun trackError(screenName: String?, errorMessage: String, errorType: String, errorStackTrace: String?) {
        val bundle = Bundle()
        bundle.putString("screen_name", screenName)
        bundle.putString("error_message", errorMessage)
        bundle.putString("error_type", errorType)
        errorStackTrace?.let {
            bundle.putString("error_stack_trace", it)
        }
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ERROR_EVENT", bundle)
    }
}

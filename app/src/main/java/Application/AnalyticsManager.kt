package Application

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

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


    // Method for handling user interactions
    fun trackUserInteraction(screenName: String, action: String) {
        val bundle = Bundle()
        bundle.putString("screen_name", screenName)
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

    fun trackError(screenName: String, errorMessage: String, errorType: String, errorStackTrace: String?) {
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

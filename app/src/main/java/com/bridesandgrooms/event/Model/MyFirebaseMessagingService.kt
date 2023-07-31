package com.bridesandgrooms.event.Model

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Handle the data payload here
            // You can extract values from the remoteMessage.data map and use them in your app
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]

            // You can then use the title and message to show a notification or update your app UI
            // For example, you can use a notification manager to show a notification to the user
            showNotification(title, message)
        }

        // Check if the message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // You can send the token to your server or perform any other action with it
        // For example, you can associate the token with the user's account on your backend server
    }

    private fun showNotification(title: String?, message: String?) {
        // Implement your notification logic here
        // You can use the NotificationManager to show a notification to the user
        // For simplicity, this example will not include the notification implementation
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

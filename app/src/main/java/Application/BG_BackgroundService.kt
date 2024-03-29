package Application
//
//import android.annotation.SuppressLint
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.os.IBinder
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.bridesandgrooms.event.TaskNotificationReceiver
//
//class BG_BackgroundService : Service() {
//
//    private val receiver = TaskNotificationReceiver()
//
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    override fun onCreate() {
//        super.onCreate()
//        Log.d("BG_BackgroundService", "BackgroundService Started")
//        registerReceiver(receiver, IntentFilter("com.bridesandgrooms.event.NOTIFICATION_RECEIVED"))
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("BG_BackgroundService", "BackgroundService Stopped")
//        unregisterReceiver(receiver)
//    }
//
//    private fun createNotificationChannel() {
//        // Create the notification channel only if the device is running Android Oreo or higher
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Foreground Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun createBlankNotification(): Notification {
//        // Create a blank notification with minimal content
//        return NotificationCompat.Builder(this, CHANNEL_ID).build()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    companion object {
//        const val NOTIFICATION_ID = 1002
//        const val CHANNEL_ID = "BackgroundService"
//    }
//}

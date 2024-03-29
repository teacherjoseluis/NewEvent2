package Application

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bridesandgrooms.event.NotificationReceiver

class BG_ReminderJobService(context: Context, params: WorkerParameters) : Worker(context, params) {

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun doWork(): Result {
        // Register the receiver
        val receiver = NotificationReceiver()
        val intent = IntentFilter("android.intent.action.USER_PRESENT")
        applicationContext.registerReceiver(receiver, intent)
        Log.d("BG_ReminderJobService", "Register Broadcast receiver")

//        Log.i("BG_ReminderJobService", "broadcast is called")
//        val intent = Intent("com.bridesandgrooms.event.NOTIFICATION_RECEIVED")
//        intent.putExtra("TASK_NAME", "MyDummyTask") // Pass taskName as an extra to the intent
//        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}


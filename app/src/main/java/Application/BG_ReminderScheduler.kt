package Application

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager

import java.util.concurrent.TimeUnit

class BG_ReminderScheduler {
    fun scheduleReminderJob(context: Context) {
        // Schedule periodic registration and un-registration of the receiver
        val registrationWorkRequest =
            PeriodicWorkRequest.Builder(BG_ReminderJobService::class.java, 15, TimeUnit.MINUTES)
                .build()
        Log.d("BG_ReminderScheduler", "Schedule Work")
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ReminderReceiverRegistrationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            registrationWorkRequest
        )
    }

    companion object {
        private const val REMINDER_JOB_ID = 1001
    }
}


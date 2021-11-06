package com.example.newevent2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


private const val NOTIF_CHANNEL_ID = "primary_notification_channel"
private const val NOTIF_CHANNEL_NAME = "Job Service notification"

class NotificationJobService : JobService() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartJob(params: JobParameters?): Boolean {

        // Get Notification Manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel if device OS >= Android O
        NotificationChannel(
            NOTIF_CHANNEL_ID,
            NOTIF_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).let {
            notificationManager.createNotificationChannel(it)
        }

        // Create PendingIntent with empty Intent
        // So this pending intent does nothing
        val myintent = Intent(this, ActivityContainer::class.java)
        myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, myintent, PendingIntent.FLAG_IMMUTABLE)

        val notificationtitle = params!!.extras.getString("title")
        val notificationbody = params.extras.getString("body")
//        val gson = Gson()
//        val task = gson.fromJson(json, Task::class.java)

        // Configure NotificationBuilder
        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(notificationtitle)
            .setContentText(notificationbody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Make the Notification
        notificationManager.notify("firebaseconnectivity", 0, builder.build())


        // False to let system know that the job is completed by the end of onStartJob(),
        // and the system calls jobFinished() to end the job.
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // True because if the job fails, you want the job to be rescheduled instead of dropped.
        return true
    }
}
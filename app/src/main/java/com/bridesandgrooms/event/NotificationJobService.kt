//package com.bridesandgrooms.event
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.job.JobParameters
//import android.app.job.JobService
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.work.Configuration
//import androidx.work.WorkManager
//
////
//private const val NOTIF_CHANNEL_ID = "primary_notification_channel"
//private const val NOTIF_CHANNEL_NAME = "Job Service notification"
//
//class NotificationJobService : JobService() {
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onStartJob(params: JobParameters?): Boolean {
//        // Define custom range of job IDs for WorkManager
//        val myConfiguration = Configuration.Builder()
//            .setJobSchedulerJobIdRange(1000, 9999) // Define custom range of job IDs
//            .build()
//
//        // Initialize WorkManager with custom configuration
//        WorkManager.initialize(this, myConfiguration)
//
//        //retrieveAndScheduleNotifications()
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create Notification Channel if device OS >= Android O
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                NOTIF_CHANNEL_ID,
//                NOTIF_CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Notification Channel Description"
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Get notification title and body from extras
//        val notificationTitle = params!!.extras.getString("title")
//        val notificationBody = params.extras.getString("body")
//
//        // Create PendingIntent to launch ActivityContainer
//        val intent = Intent(this, ActivityContainer::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // Build the notification
//        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(notificationTitle)
//            .setContentText(notificationBody)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        // Show the notification
//        notificationManager.notify(NOTIFICATION_ID, builder.build())
//
//        // Indicate that the job is completed
//        return false
//    }
//
//    override fun onStopJob(params: JobParameters?): Boolean {
//        // Return true to reschedule the job if it's stopped prematurely
//        return true
//    }
//}

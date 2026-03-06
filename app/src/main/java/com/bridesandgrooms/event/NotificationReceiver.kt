package com.bridesandgrooms.event

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.TaskDBHelper
import java.util.Date

class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val currentDate = Date(System.currentTimeMillis())
        val taskDBHelper = TaskDBHelper()
        val paymentDBHelper = PaymentDBHelper()

        try {
            val taskList = taskDBHelper.getTaskfromDate(currentDate) ?: arrayListOf()
            val paymentList = paymentDBHelper.getPaymentfromDate(currentDate) ?: arrayListOf()

            if (taskList.isNotEmpty() || paymentList.isNotEmpty()) {
                buildNotification(context, taskList, paymentList)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildNotification(
        context: Context,
        taskList: ArrayList<String>,
        paymentList: ArrayList<String>
    ) {
        // Create the notification channel (safe to call repeatedly)
        createNotificationChannel(context)

        val openAppIntent = Intent(context, ActivityContainer::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // ✅ Compatible PendingIntent flags for all supported APIs
        val pendingIntentFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            pendingIntentFlags
        )

        // Task Section
        val taskBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.weddingicon)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setGroup(TASK_REMINDER)
            setGroupSummary(true)
        }

        if (taskList.isNotEmpty()) {
            val inboxStyleTask = NotificationCompat.InboxStyle().also { style ->
                taskList.forEach { style.addLine(it) }
            }
            taskBuilder
                .setContentTitle(context.getString(R.string.task_reminder))
                .setContentText(context.getString(R.string.you_have_tasks, taskList.size.toString()))
                .setStyle(inboxStyleTask)
        }

        // Payment Section
        val paymentBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.weddingicon)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setGroup(PAYMENT_REMINDER)
            setGroupSummary(true)
        }

        if (paymentList.isNotEmpty()) {
            val inboxStylePayment = NotificationCompat.InboxStyle().also { style ->
                paymentList.forEach { style.addLine(it) }
            }
            paymentBuilder
                .setContentTitle(context.getString(R.string.payment_reminder))
                .setContentText(context.getString(R.string.you_have_payments, paymentList.size.toString()))
                .setStyle(inboxStylePayment)
        }

        // ✅ API 33+ requires POST_NOTIFICATIONS permission at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!PermissionUtils.checkPermissions(context, "notification")) {
                // Can't request permission from a BroadcastReceiver safely.
                // Just skip the notification; request permission from an Activity in your app UX.
                Log.w(TAG, "POST_NOTIFICATIONS not granted; skipping notification.")
                return
            }
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (taskList.isNotEmpty()) {
                notificationManager.notify(TASKREMINDER_NOTIFICATION_ID, taskBuilder.build())
            }
            if (paymentList.isNotEmpty()) {
                notificationManager.notify(PAYMENTREMINDER_NOTIFICATION_ID, paymentBuilder.build())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when posting notification: ${e.message}")
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task reminders"
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "taskpayment_channel"
        private const val TASKREMINDER_NOTIFICATION_ID = 1001
        private const val PAYMENTREMINDER_NOTIFICATION_ID = 1002
        private const val TASK_REMINDER = "com.bridesandgrooms.event.TASK_REMINDER"
        private const val PAYMENT_REMINDER = "com.bridesandgrooms.event.PAYMENT_REMINDER"
        private const val TAG = "NotificationReceiver"
        const val NOTIFICATION_PERMISSION_CODE = 103
    }
}

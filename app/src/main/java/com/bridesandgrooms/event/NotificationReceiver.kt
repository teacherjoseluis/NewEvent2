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

    override fun onReceive(context: Context, intent: Intent) {
        val currentDate = Date(System.currentTimeMillis())
        val taskDBHelper = TaskDBHelper()
        val paymentDBHelper = PaymentDBHelper()

        try {
            val taskList = taskDBHelper.getTaskfromDate(currentDate)
            val paymentList = paymentDBHelper.getPaymentfromDate(currentDate)

            if (!taskList.isNullOrEmpty() || !paymentList.isNullOrEmpty()){
                buildNotification(context, taskList!!, paymentList!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
//        val taskList = arrayOf("task1", "task2", "task3")
//        val paymentList = arrayOf("payment1", "payment2", "payment3")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildNotification(
        context: Context,
        taskList: ArrayList<String>,
        paymentList: ArrayList<String>
    ) {
        // Create the notification
        createNotificationChannel(context)

        val intent = Intent(context, ActivityContainer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Task Section
        val taskBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        if (!taskList.isNullOrEmpty()) {
            val inboxStyleTask = NotificationCompat.InboxStyle()
            taskList.forEach {
                inboxStyleTask.addLine(it)
            }

            with(taskBuilder) {
                setContentTitle(context.getString(R.string.task_reminder))
                setContentText(context.getString(R.string.you_have_tasks, taskList.size.toString()))
                setSmallIcon(R.drawable.weddingicon)
                priority = NotificationCompat.PRIORITY_HIGH
                setAutoCancel(true)
                setContentIntent(pendingIntent)
                setGroup(TASK_REMINDER)
                setGroupSummary(true)
                setStyle(inboxStyleTask)
            }
        }

        // Payment Section
        val paymentBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        if (!paymentList.isNullOrEmpty()) {
            val inboxStylePayment = NotificationCompat.InboxStyle()
            paymentList.forEach {
                inboxStylePayment.addLine(it)
            }
            with(paymentBuilder) {
                setContentTitle(context.getString(R.string.payment_reminder))
                setContentText(context.getString(R.string.you_have_payments, paymentList.size.toString()))
                setSmallIcon(R.drawable.weddingicon)
                priority = NotificationCompat.PRIORITY_HIGH
                setAutoCancel(true)
                setContentIntent(pendingIntent)
                setGroup(PAYMENT_REMINDER)
                setGroupSummary(true)
                setStyle(inboxStylePayment)
            }
        }

        if (!PermissionUtils.checkPermissions(context, "notification")) {
            if (context is Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissions = PermissionUtils.requestPermissionsList("notification")
                ActivityCompat.requestPermissions(context, permissions, NOTIFICATION_PERMISSION_CODE)
            }
            return
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            // Use notificationManager here as needed
            with(notificationManager) {
                notify(TASKREMINDER_NOTIFICATION_ID, taskBuilder.build())
                notify(PAYMENTREMINDER_NOTIFICATION_ID, paymentBuilder.build())
            }
        } catch (e: SecurityException) {
            Log.e("Notification", "Security exception when accessing NotificationManager: ${e.message}")
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

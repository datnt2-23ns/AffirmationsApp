package com.example.ticketbookingapp.Utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.ticketbookingapp.Activities.Splash.SplashActivity
import com.example.ticketbookingapp.R

class FlightNotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID = "flight_reminder_channel"
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val FLIGHT_INFO_KEY = "flight_info"
        const val DEFAULT_NOTIFICATION_ID = 1001
    }

    override fun doWork(): Result {
        Log.d("FlightNotification", "Worker triggered! Sending notification...")
        createNotificationChannel(applicationContext)

        val flightInfo = inputData.getString(FLIGHT_INFO_KEY) ?: "Chuyến bay sắp khởi hành!"
        val notificationId = inputData.getInt(NOTIFICATION_ID_KEY, DEFAULT_NOTIFICATION_ID)

        val openAppIntent = Intent(applicationContext, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Nhắc nhở chuyến bay")
            .setContentText(flightInfo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("FlightNotification", "Permission POST_NOTIFICATIONS not granted")
            return Result.failure()
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }

        return Result.success()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nhắc nhở chuyến bay"
            val descriptionText = "Thông báo các chuyến bay sắp khởi hành"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
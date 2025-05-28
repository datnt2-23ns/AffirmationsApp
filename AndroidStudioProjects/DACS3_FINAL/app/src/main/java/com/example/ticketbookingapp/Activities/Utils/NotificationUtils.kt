package com.example.ticketbookingapp.Utils

import android.content.Context
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationUtils {

    fun scheduleFlightNotification(
        context: Context,
        flightId: String,
        flightInfoText: String,
        flightDateString: String, // "20 May, 2025"
        flightTimeString: String  // "14:30"
    ) {
        try {
            val dateTimeStr = "$flightDateString $flightTimeString"
            val sdf = SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.ENGLISH).apply {
                timeZone = TimeZone.getDefault()
            }
            val flightTime = sdf.parse(dateTimeStr) ?: return // Return if parsing fails

            val notifyTimeMillis = flightTime.time - 60 * 60 * 1000 // 1 hour before flight
            val delayMillis = notifyTimeMillis - System.currentTimeMillis()

            // Only schedule if the notification time is in the future
            if (delayMillis > 0) {
                val inputData = Data.Builder()
                    .putString("flight_info", flightInfoText)
                    .putInt("notification_id", flightId.hashCode())
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<FlightNotificationWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("flight_$flightId")
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
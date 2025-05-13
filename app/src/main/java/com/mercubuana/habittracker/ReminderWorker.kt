package com.mercubuana.habittracker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        val habitId = inputData.getInt("habitId", -1)
        val habitName = inputData.getString("habitName") ?: return Result.failure()

        val notif = NotificationCompat.Builder(applicationContext, "habit_channel")
            .setSmallIcon(R.drawable.ic_notification)          // make sure drawable exists
            .setContentTitle("Habit Reminder")
            .setContentText("Time to complete: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val nm = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(habitId, notif)

        return Result.success()
    }
}

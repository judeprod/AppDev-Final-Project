package com.saveourwater.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saveourwater.R
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.ui.main.MainActivity

/**
 * NotificationHelper - Manages achievement notifications
 * PHASE3-FEAT-P1-030: Build Achievement Notification System
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ACHIEVEMENTS = "achievements_channel"
        const val CHANNEL_REMINDERS = "reminders_channel"
        private const val NOTIFICATION_ID_BASE = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Achievements channel
            val achievementsChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievement Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for unlocked achievements"
                enableVibration(true)
                enableLights(true)
                lightColor = Color.BLUE
            }

            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily water tracking reminders"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(achievementsChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }

    /**
     * Show notification when achievement is unlocked
     */
    fun showAchievementUnlocked(achievement: Achievement) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "achievements")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val tierEmoji = when (achievement.tier.name) {
            "BRONZE" -> "🥉"
            "SILVER" -> "🥈"
            "GOLD" -> "🥇"
            "PLATINUM" -> "💎"
            else -> "🏆"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_achievements)
            .setContentTitle("$tierEmoji Achievement Unlocked!")
            .setContentText(achievement.name)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${achievement.name}\n${achievement.description}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_achievements,
                "View Achievements",
                pendingIntent
            )
            .setColor(context.getColor(R.color.primary_500))
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                NOTIFICATION_ID_BASE + achievement.id.hashCode(),
                notification
            )
        }
    }

    /**
     * Show daily reminder notification
     */
    fun showDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("💧 Time to Track Your Water!")
            .setContentText("Don't forget to log your water usage today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(2001, notification)
        }
    }
}

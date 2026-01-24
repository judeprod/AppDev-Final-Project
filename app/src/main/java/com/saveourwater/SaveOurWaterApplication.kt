package com.saveourwater

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

/**
 * Application class for Save Our Water app
 * Handles app-wide initialization
 */
class SaveOurWaterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize app-wide configurations
        initializeAppTheme()
        createNotificationChannels()
    }

    /**
     * Initialize app theme based on user preferences
     */
    private fun initializeAppTheme() {
        // Default to system theme, will be overridden by user settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Achievement notification channel
            val achievementChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for unlocked achievements"
                enableVibration(true)
            }

            // Daily reminder channel
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Daily reminders to log water usage"
            }

            // Goal milestone channel
            val milestoneChannel = NotificationChannel(
                CHANNEL_MILESTONES,
                "Goal Milestones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you reach conservation milestones"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(achievementChannel)
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(milestoneChannel)
        }
    }

    companion object {
        const val CHANNEL_ACHIEVEMENTS = "achievements"
        const val CHANNEL_REMINDERS = "reminders"
        const val CHANNEL_MILESTONES = "milestones"
    }
}

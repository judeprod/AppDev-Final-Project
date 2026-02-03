package com.saveourwater

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.saveourwater.data.local.database.AppDatabase
import com.saveourwater.data.remote.SupabaseModule
import com.saveourwater.data.repository.WaterRepository
import com.saveourwater.data.sync.SyncManager
import com.saveourwater.data.sync.SyncWorker
import com.saveourwater.domain.AchievementManager
import com.saveourwater.utils.NotificationHelper

/**
 * Application class for Save Our Water app
 * Handles app-wide initialization and dependency injection
 * PHASE3-FEAT-P0-024: Added AchievementManager initialization
 * Supabase Sync: Added SyncManager and SyncWorker initialization
 */
class SaveOurWaterApplication : Application() {

    // Lazy-init database
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    // Lazy-init repository
    val waterRepository: WaterRepository by lazy {
        WaterRepository(
            database.waterActivityDao(),
            database.ecoGoalDao()
        )
    }
    
    // Lazy-init notification helper
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }
    
    // Lazy-init achievement manager
    val achievementManager: AchievementManager by lazy {
        AchievementManager(
            database.achievementDao(),
            waterRepository,
            notificationHelper
        )
    }
    
    // Lazy-init sync manager
    val syncManager: SyncManager by lazy {
        SyncManager(this, database.waterActivityDao())
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize app-wide configurations
        initializeAppTheme()
        createNotificationChannels()
        // Note: Supabase and sync are initialized lazily on first use
        // This prevents crashes if there are network issues on startup
    }

    /**
     * Initialize app theme based on user preferences
     */
    private fun initializeAppTheme() {
        // Default to system theme, will be overridden by user settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    /**
     * Initialize Supabase client
     */
    private fun initializeSupabase() {
        try {
            // Access the client to trigger lazy initialization
            if (SupabaseModule.isConfigured) {
                Log.d(TAG, "Supabase client initialized successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Supabase: ${e.message}")
        }
    }

    /**
     * Schedule periodic background sync
     */
    private fun schedulePeriodicSync() {
        try {
            SyncWorker.schedulePeriodicSync(this)
            Log.d(TAG, "Periodic sync scheduled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule sync: ${e.message}")
        }
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
        private const val TAG = "SaveOurWaterApp"
        const val CHANNEL_ACHIEVEMENTS = "achievements"
        const val CHANNEL_REMINDERS = "reminders"
        const val CHANNEL_MILESTONES = "milestones"
    }
}



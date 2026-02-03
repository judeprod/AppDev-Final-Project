package com.saveourwater.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.saveourwater.data.local.database.AppDatabase
import java.util.concurrent.TimeUnit

/**
 * SyncWorker - Background worker for syncing local data with Supabase
 * Uses WorkManager for reliable background execution
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        private const val UNIQUE_WORK_NAME = "supabase_sync"
        private const val PERIODIC_WORK_NAME = "supabase_periodic_sync"

        /**
         * Schedule a one-time sync (e.g., after logging an activity)
         */
        fun scheduleOneTimeSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    UNIQUE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    syncRequest
                )

            Log.d(TAG, "Scheduled one-time sync")
        }

        /**
         * Schedule periodic sync (e.g., every 15 minutes when network available)
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    PERIODIC_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
                )

            Log.d(TAG, "Scheduled periodic sync every 15 minutes")
        }

        /**
         * Cancel all sync work
         */
        fun cancelAllSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
            Log.d(TAG, "Cancelled all sync work")
        }
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting sync work...")

        return try {
            // Get database and create SyncManager
            val database = AppDatabase.getDatabase(applicationContext)
            val syncManager = SyncManager(
                context = applicationContext,
                waterActivityDao = database.waterActivityDao()
            )

            // Push pending local changes to cloud
            when (val pushResult = syncManager.syncPendingToCloud()) {
                is SyncResult.Success -> {
                    Log.d(TAG, "Pushed ${pushResult.count} activities to cloud")
                }
                is SyncResult.NotAuthenticated -> {
                    Log.w(TAG, "User not authenticated, skipping sync")
                    return Result.success() // Don't retry if not authenticated
                }
                is SyncResult.NoNetwork -> {
                    Log.w(TAG, "No network, will retry later")
                    return Result.retry()
                }
                is SyncResult.NotConfigured -> {
                    Log.w(TAG, "Supabase not configured")
                    return Result.success()
                }
                is SyncResult.Error -> {
                    Log.e(TAG, "Push error: ${pushResult.message}")
                    return Result.retry()
                }
            }

            // Pull remote changes (using last sync timestamp from preferences)
            // For simplicity, we're using 0 here - in production, store/retrieve last sync time
            val lastSyncTimestamp = 0L
            when (val pullResult = syncManager.pullFromCloud(lastSyncTimestamp)) {
                is SyncResult.Success -> {
                    Log.d(TAG, "Pulled ${pullResult.count} activities from cloud")
                }
                is SyncResult.Error -> {
                    Log.e(TAG, "Pull error: ${pullResult.message}")
                    // Don't fail the whole job for pull errors
                }
                else -> { /* Already handled in push */ }
            }

            Log.d(TAG, "Sync completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Sync failed with exception", e)
            Result.retry()
        }
    }
}

package com.saveourwater.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.saveourwater.data.local.dao.WaterActivityDao
import com.saveourwater.data.local.entities.SyncStatus
import com.saveourwater.data.local.entities.WaterActivity
import com.saveourwater.data.remote.SupabaseModule
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * SyncManager - Handles bidirectional sync between Room and Supabase
 * Part 4: Sync Strategy Implementation
 */
class SyncManager(
    private val context: Context,
    private val waterActivityDao: WaterActivityDao
) {
    companion object {
        private const val TAG = "SyncManager"
        private const val TABLE_WATER_ACTIVITIES = "water_activities"
    }

    /**
     * Check if network is available
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Sync pending local activities to Supabase
     * Call this when network becomes available or on app startup
     */
    suspend fun syncPendingToCloud(): SyncResult = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext SyncResult.NoNetwork
        }

        if (!SupabaseModule.isConfigured) {
            return@withContext SyncResult.NotConfigured
        }

        try {
            val pendingActivities = waterActivityDao.getActivitiesBySyncStatus(SyncStatus.PENDING)
            
            if (pendingActivities.isEmpty()) {
                return@withContext SyncResult.Success(0)
            }

            val userId = SupabaseModule.client.auth.currentUserOrNull()?.id
            if (userId == null) {
                Log.w(TAG, "User not authenticated, skipping sync")
                return@withContext SyncResult.NotAuthenticated
            }

            var syncedCount = 0
            
            for (activity in pendingActivities) {
                try {
                    val remoteActivity = RemoteWaterActivity(
                        userId = userId,
                        activityType = activity.activityType.name,
                        litersUsed = activity.litersUsed,
                        durationMinutes = activity.durationSeconds / 60,
                        waterSource = activity.waterSource,
                        flowPressure = activity.flowPressure,
                        isEcoMode = activity.isEcoMode,
                        notes = activity.notes,
                        timestamp = java.time.Instant.ofEpochMilli(activity.timestamp).toString(),
                        localId = activity.id.toString()
                    )

                    val response = SupabaseModule.client.postgrest[TABLE_WATER_ACTIVITIES]
                        .insert(remoteActivity) {
                            select()
                        }
                        .decodeSingle<RemoteWaterActivityResponse>()

                    // Update local record with remote ID and mark as synced
                    val updatedActivity = activity.copy(
                        remoteId = response.id,
                        syncStatus = SyncStatus.SYNCED,
                        lastModified = System.currentTimeMillis()
                    )
                    waterActivityDao.update(updatedActivity)
                    syncedCount++

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync activity ${activity.id}: ${e.message}")
                }
            }

            SyncResult.Success(syncedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}", e)
            SyncResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Pull remote changes and merge to local Room database
     */
    suspend fun pullFromCloud(lastSyncTimestamp: Long): SyncResult = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext SyncResult.NoNetwork
        }

        if (!SupabaseModule.isConfigured) {
            return@withContext SyncResult.NotConfigured
        }

        try {
            val userId = SupabaseModule.client.auth.currentUserOrNull()?.id
                ?: return@withContext SyncResult.NotAuthenticated

            val remoteActivities = SupabaseModule.client.postgrest[TABLE_WATER_ACTIVITIES]
                .select {
                    filter {
                        eq("user_id", userId)
                        gt("synced_at", java.time.Instant.ofEpochMilli(lastSyncTimestamp).toString())
                    }
                }
                .decodeList<RemoteWaterActivityResponse>()

            var mergedCount = 0

            for (remote in remoteActivities) {
                // Check if we already have this record locally
                val existingLocal = remote.localId?.let { 
                    waterActivityDao.getById(it.toLongOrNull() ?: 0)
                }

                if (existingLocal == null) {
                    // New record from cloud - insert locally
                    val newLocal = WaterActivity(
                        activityType = com.saveourwater.data.local.entities.ActivityType.valueOf(remote.activityType),
                        litersUsed = remote.litersUsed,
                        durationSeconds = (remote.durationMinutes ?: 0) * 60,
                        waterSource = remote.waterSource,
                        flowPressure = remote.flowPressure,
                        isEcoMode = remote.isEcoMode ?: false,
                        notes = remote.notes,
                        timestamp = java.time.Instant.parse(remote.timestamp).toEpochMilli(),
                        remoteId = remote.id,
                        syncStatus = SyncStatus.SYNCED,
                        lastModified = System.currentTimeMillis()
                    )
                    waterActivityDao.insert(newLocal)
                    mergedCount++
                }
                // Note: For simplicity, we're not handling update conflicts here
                // In production, you'd compare timestamps and handle conflicts
            }

            SyncResult.Success(mergedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Pull failed: ${e.message}", e)
            SyncResult.Error(e.message ?: "Unknown error")
        }
    }
}

/**
 * Sync operation result
 */
sealed class SyncResult {
    data class Success(val count: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
    data object NoNetwork : SyncResult()
    data object NotConfigured : SyncResult()
    data object NotAuthenticated : SyncResult()
}

/**
 * Data class for sending activities to Supabase
 */
@Serializable
data class RemoteWaterActivity(
    val userId: String,
    val activityType: String,
    val litersUsed: Double,
    val durationMinutes: Int? = null,
    val waterSource: String? = null,
    val flowPressure: String? = null,
    val isEcoMode: Boolean? = null,
    val notes: String? = null,
    val timestamp: String,
    val localId: String? = null
)

/**
 * Data class for receiving activities from Supabase
 */
@Serializable
data class RemoteWaterActivityResponse(
    val id: String,
    val userId: String,
    val activityType: String,
    val litersUsed: Double,
    val durationMinutes: Int? = null,
    val waterSource: String? = null,
    val flowPressure: String? = null,
    val isEcoMode: Boolean? = null,
    val notes: String? = null,
    val timestamp: String,
    val localId: String? = null,
    val syncedAt: String? = null
)

package com.saveourwater.data.local.dao

import androidx.room.*
import com.saveourwater.data.local.entities.WaterActivity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WaterActivity
 * PHASE1-DB-P0-005: Create DAO Interfaces
 */
@Dao
interface WaterActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: WaterActivity): Long

    @Update
    suspend fun update(activity: WaterActivity)

    @Delete
    suspend fun delete(activity: WaterActivity)

    @Query("SELECT * FROM water_activities ORDER BY timestamp DESC")
    fun getAllActivitiesFlow(): Flow<List<WaterActivity>>

    @Query("SELECT * FROM water_activities ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivities(limit: Int): Flow<List<WaterActivity>>

    @Query("SELECT * FROM water_activities WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getActivitiesBetween(startTime: Long, endTime: Long): Flow<List<WaterActivity>>

    @Query("SELECT SUM(litersUsed) FROM water_activities WHERE timestamp BETWEEN :startTime AND :endTime")
    fun getTotalUsageBetween(startTime: Long, endTime: Long): Flow<Double?>

    @Query("SELECT SUM(litersUsed) FROM water_activities WHERE timestamp >= :startOfDay")
    fun getTodayUsage(startOfDay: Long): Flow<Double?>

    @Query("SELECT COUNT(DISTINCT date(timestamp/1000, 'unixepoch')) FROM water_activities")
    suspend fun getDaysWithActivities(): Int

    @Query("SELECT * FROM water_activities WHERE syncedToCloud = 0")
    suspend fun getUnsyncedActivities(): List<WaterActivity>

    @Query("UPDATE water_activities SET cloudId = :cloudId, syncedToCloud = 1 WHERE id = :localId")
    suspend fun markAsSynced(localId: Long, cloudId: String?)
}

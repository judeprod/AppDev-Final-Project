package com.saveourwater.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WaterActivity Entity - Stores individual water usage events
 * PHASE1-DB-P0-004: Design Room Database Schema
 * Updated for Supabase Hybrid Sync
 */
@Entity(tableName = "water_activities")
data class WaterActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Core activity data
    @ColumnInfo(name = "activity_type") val activityType: ActivityType,
    @ColumnInfo(name = "liters_used") val litersUsed: Double,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    
    // Estimation fields (from DetailedTracking)
    @ColumnInfo(name = "water_source") val waterSource: String? = null,
    @ColumnInfo(name = "flow_pressure") val flowPressure: String? = null,
    @ColumnInfo(name = "is_eco_mode") val isEcoMode: Boolean = false,
    
    // Sync fields for Supabase
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "last_modified") val lastModified: Long = System.currentTimeMillis()
)

/**
 * ActivityType Enum - Types of water-consuming activities
 */
enum class ActivityType {
    SHOWER,
    TAP,
    TOILET,
    LAUNDRY,
    DISHES,
    GARDEN,
    CUSTOM
}


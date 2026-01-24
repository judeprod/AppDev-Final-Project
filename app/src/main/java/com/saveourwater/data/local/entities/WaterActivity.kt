package com.saveourwater.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WaterActivity Entity - Stores individual water usage events
 * PHASE1-DB-P0-004: Design Room Database Schema
 */
@Entity(tableName = "water_activities")
data class WaterActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityType: ActivityType,
    val litersUsed: Double,
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val syncedToCloud: Boolean = false,
    val cloudId: String? = null
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

package com.saveourwater.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Achievement Entity - Stores user achievements and unlock status
 * PHASE3-FEAT-P0-023: Define Achievement System
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val iconResId: Int,
    val targetValue: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0
)

/**
 * AchievementCategory Enum - Categories for achievement types
 */
enum class AchievementCategory {
    STREAKS,        // Consecutive days of tracking
    CONSERVATION,   // Water saved milestones
    BEHAVIORAL      // Usage patterns and habits
}

/**
 * AchievementTier Enum - Achievement difficulty tiers
 */
enum class AchievementTier {
    BRONZE,     // Easy achievements
    SILVER,     // Medium achievements  
    GOLD,       // Hard achievements
    PLATINUM    // Expert achievements
}

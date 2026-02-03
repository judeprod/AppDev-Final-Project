package com.saveourwater.data.local.database

import androidx.room.TypeConverter
import com.saveourwater.data.local.entities.ActivityType
import com.saveourwater.data.local.entities.AchievementCategory
import com.saveourwater.data.local.entities.AchievementTier
import com.saveourwater.data.local.entities.SyncStatus

/**
 * Type Converters for Room Database
 * PHASE3-DB-P1-025: Added Achievement enum converters
 * Supabase Sync: Added SyncStatus converter
 */
class Converters {
    // ActivityType converters
    @TypeConverter
    fun fromActivityType(value: ActivityType): String {
        return value.name
    }

    @TypeConverter
    fun toActivityType(value: String): ActivityType {
        return ActivityType.valueOf(value)
    }

    // AchievementCategory converters
    @TypeConverter
    fun fromAchievementCategory(value: AchievementCategory): String {
        return value.name
    }

    @TypeConverter
    fun toAchievementCategory(value: String): AchievementCategory {
        return AchievementCategory.valueOf(value)
    }

    // AchievementTier converters
    @TypeConverter
    fun fromAchievementTier(value: AchievementTier): String {
        return value.name
    }

    @TypeConverter
    fun toAchievementTier(value: String): AchievementTier {
        return AchievementTier.valueOf(value)
    }

    // SyncStatus converters for Supabase sync
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}


package com.saveourwater.data.local.database

import androidx.room.TypeConverter
import com.saveourwater.data.local.entities.ActivityType

/**
 * Type Converters for Room Database
 */
class Converters {
    @TypeConverter
    fun fromActivityType(value: ActivityType): String {
        return value.name
    }

    @TypeConverter
    fun toActivityType(value: String): ActivityType {
        return ActivityType.valueOf(value)
    }
}

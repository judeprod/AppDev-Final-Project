package com.saveourwater.data

import com.saveourwater.R
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementCategory
import com.saveourwater.data.local.entities.AchievementTier

/**
 * AchievementDefinitions - Predefined achievements for the app
 * PHASE3-FEAT-P0-023: Define Achievement System
 */
object AchievementDefinitions {
    
    val ALL_ACHIEVEMENTS = listOf(
        // ============ STREAK ACHIEVEMENTS ============
        Achievement(
            id = "streak_3_days",
            name = "Getting Started",
            description = "Log water usage for 3 consecutive days",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 3
        ),
        Achievement(
            id = "streak_7_days",
            name = "Week Warrior",
            description = "Maintain a 7-day tracking streak",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 7
        ),
        Achievement(
            id = "streak_14_days",
            name = "Two Week Champion",
            description = "Keep your streak going for 14 days",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.GOLD,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 14
        ),
        Achievement(
            id = "streak_30_days",
            name = "Monthly Master",
            description = "Achieve a legendary 30-day tracking streak",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.PLATINUM,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 30
        ),
        
        // ============ CONSERVATION ACHIEVEMENTS ============
        Achievement(
            id = "save_100_liters",
            name = "Drop Saver",
            description = "Save 100 liters compared to average usage",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 100
        ),
        Achievement(
            id = "save_500_liters",
            name = "Water Guardian",
            description = "Save 500 liters of water",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 500
        ),
        Achievement(
            id = "save_1000_liters",
            name = "Conservation Hero",
            description = "Save 1000 liters - that's enough for a family!",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.GOLD,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 1000
        ),
        Achievement(
            id = "save_5000_liters",
            name = "Planet Protector",
            description = "Save 5000 liters - you're making a real difference!",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.PLATINUM,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 5000
        ),
        
        // ============ BEHAVIORAL ACHIEVEMENTS ============
        Achievement(
            id = "first_activity",
            name = "First Drop",
            description = "Log your first water activity",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_first,
            targetValue = 1
        ),
        Achievement(
            id = "eco_mode_5",
            name = "Eco Starter",
            description = "Use Eco-Mode 5 times while tracking",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_eco,
            targetValue = 5
        ),
        Achievement(
            id = "under_goal_7",
            name = "Goal Crusher",
            description = "Stay under your daily goal for 7 days",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_goal,
            targetValue = 7
        ),
        Achievement(
            id = "all_activity_types",
            name = "Well Rounded",
            description = "Log all 6 activity types at least once",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_variety,
            targetValue = 6
        )
    )
}

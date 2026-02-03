package com.saveourwater.domain

import com.saveourwater.data.local.dao.AchievementDao
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementCategory
import com.saveourwater.data.repository.WaterRepository
import com.saveourwater.utils.NotificationHelper
import kotlinx.coroutines.flow.Flow

/**
 * AchievementManager - Manages achievement progress and unlocking
 * PHASE3-FEAT-P0-024: Implement AchievementManager
 */
class AchievementManager(
    private val achievementDao: AchievementDao,
    private val waterRepository: WaterRepository,
    private val notificationHelper: NotificationHelper
) {

    // ===================== Achievement Queries =====================

    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievementsFlow()
    }

    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>> {
        return achievementDao.getAchievementsByCategory(category)
    }

    fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements()
    }

    fun getUnlockedCount(): Flow<Int> {
        return achievementDao.getUnlockedCount()
    }

    fun getTotalCount(): Flow<Int> {
        return achievementDao.getTotalCount()
    }

    // ===================== Achievement Checking =====================

    /**
     * Check all achievements and unlock any that meet criteria
     * Should be called after each water activity is logged
     */
    suspend fun checkAchievements() {
        val lockedAchievements = achievementDao.getLockedAchievements()
        
        for (achievement in lockedAchievements) {
            val progress = calculateProgress(achievement)
            
            if (progress >= achievement.targetValue) {
                unlockAchievement(achievement)
            } else {
                // Update progress
                updateProgress(achievement, progress)
            }
        }
    }

    /**
     * Calculate current progress towards an achievement
     */
    private suspend fun calculateProgress(achievement: Achievement): Int {
        return when (achievement.id) {
            // Streak achievements
            "streak_3_days", "streak_7_days", "streak_14_days", "streak_30_days" -> {
                waterRepository.getCurrentStreak()
            }
            
            // Conservation achievements (simplified for now)
            "save_100_liters", "save_500_liters", "save_1000_liters", "save_5000_liters" -> {
                // TODO: Implement actual savings calculation vs baseline
                0
            }
            
            // Behavioral achievements
            "first_activity" -> {
                waterRepository.getTotalActivities()
            }
            
            "eco_mode_5" -> {
                // TODO: Track eco mode usage
                0
            }
            
            "under_goal_7" -> {
                // TODO: Track days under goal
                0
            }
            
            "all_activity_types" -> {
                waterRepository.getUniqueActivityTypesLogged()
            }
            
            else -> 0
        }
    }

    /**
     * Unlock an achievement
     */
    private suspend fun unlockAchievement(achievement: Achievement) {
        val unlockedAchievement = achievement.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis(),
            progress = achievement.targetValue
        )
        achievementDao.update(unlockedAchievement)
        
        // Show notification
        notificationHelper.showAchievementUnlocked(unlockedAchievement)
    }

    /**
     * Update achievement progress without unlocking
     */
    private suspend fun updateProgress(achievement: Achievement, progress: Int) {
        if (achievement.progress != progress) {
            achievementDao.update(achievement.copy(progress = progress))
        }
    }

    /**
     * Check a single achievement by ID
     */
    suspend fun checkSingleAchievement(achievementId: String) {
        val achievement = achievementDao.getById(achievementId) ?: return
        if (achievement.isUnlocked) return
        
        val progress = calculateProgress(achievement)
        if (progress >= achievement.targetValue) {
            unlockAchievement(achievement)
        } else {
            updateProgress(achievement, progress)
        }
    }
}

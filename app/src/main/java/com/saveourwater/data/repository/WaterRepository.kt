package com.saveourwater.data.repository

import com.saveourwater.data.local.dao.EcoGoalDao
import com.saveourwater.data.local.dao.WaterActivityDao
import com.saveourwater.data.local.entities.EcoGoal
import com.saveourwater.data.local.entities.WaterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Repository for Water data operations
 * PHASE2-FEAT-P0-015: Build WaterRepository
 * PHASE3-FEAT-P2-032: Added streak calculation methods
 * 
 * Implements offline-first strategy with Room Database
 */
class WaterRepository(
    private val waterActivityDao: WaterActivityDao,
    private val ecoGoalDao: EcoGoalDao
) {

    // ===================== Water Activities =====================

    fun getAllActivities(): Flow<List<WaterActivity>> {
        return waterActivityDao.getAllActivitiesFlow()
    }

    fun getRecentActivities(limit: Int = 10): Flow<List<WaterActivity>> {
        return waterActivityDao.getRecentActivities(limit)
    }

    fun getTodayUsage(): Flow<Double?> {
        val startOfDay = getStartOfDay()
        return waterActivityDao.getTodayUsage(startOfDay)
    }

    fun getUsageBetween(startTime: Long, endTime: Long): Flow<Double?> {
        return waterActivityDao.getTotalUsageBetween(startTime, endTime)
    }

    suspend fun insertActivity(activity: WaterActivity): Long {
        return withContext(Dispatchers.IO) {
            waterActivityDao.insert(activity)
        }
    }

    suspend fun updateActivity(activity: WaterActivity) {
        withContext(Dispatchers.IO) {
            waterActivityDao.update(activity)
        }
    }

    suspend fun deleteActivity(activity: WaterActivity) {
        withContext(Dispatchers.IO) {
            waterActivityDao.delete(activity)
        }
    }

    suspend fun getDaysWithActivities(): Int {
        return withContext(Dispatchers.IO) {
            waterActivityDao.getDaysWithActivities()
        }
    }

    // ===================== Eco Goals =====================

    suspend fun getActiveGoal(): EcoGoal? {
        return withContext(Dispatchers.IO) {
            ecoGoalDao.getActiveGoal()
        }
    }

    fun getActiveGoalFlow(): Flow<EcoGoal?> {
        return ecoGoalDao.getActiveGoalFlow()
    }

    suspend fun setGoal(dailyLimitLiters: Double): Long {
        return withContext(Dispatchers.IO) {
            val goal = EcoGoal(
                dailyLimitLiters = dailyLimitLiters,
                startDate = System.currentTimeMillis(),
                isActive = true
            )
            val id = ecoGoalDao.insert(goal)
            ecoGoalDao.deactivateOtherGoals(id)
            id
        }
    }

    // ===================== Streak & Achievement Tracking =====================

    /**
     * Calculate current consecutive days streak
     * PHASE3-FEAT-P2-032: Implement Streak Calculation
     */
    suspend fun getCurrentStreak(): Int {
        return withContext(Dispatchers.IO) {
            val activities = waterActivityDao.getAllActivitiesFlow().first()
            calculateStreak(activities)
        }
    }

    private fun calculateStreak(activities: List<WaterActivity>): Int {
        if (activities.isEmpty()) return 0

        val today = getDateOnly(System.currentTimeMillis())
        val yesterday = getDateOnly(today - 24 * 60 * 60 * 1000)

        // Get unique dates with activities, sorted descending
        val datesWithActivities = activities
            .map { getDateOnly(it.timestamp) }
            .distinct()
            .sortedDescending()

        if (datesWithActivities.isEmpty()) return 0

        // Check if today or yesterday has activity
        val latestDate = datesWithActivities.first()
        if (latestDate != today && latestDate != yesterday) {
            return 0 // Streak broken
        }

        var streak = 0
        var expectedDate = if (latestDate == today) today else yesterday

        for (date in datesWithActivities) {
            if (date == expectedDate) {
                streak++
                expectedDate = getDateOnly(expectedDate - 24 * 60 * 60 * 1000)
            } else if (date < expectedDate) {
                break // Gap in days, streak ends
            }
        }

        return streak
    }

    /**
     * Get total number of activities logged
     */
    suspend fun getTotalActivities(): Int {
        return withContext(Dispatchers.IO) {
            waterActivityDao.getAllActivitiesFlow().first().size
        }
    }

    /**
     * Get count of unique activity types logged
     */
    suspend fun getUniqueActivityTypesLogged(): Int {
        return withContext(Dispatchers.IO) {
            val activities = waterActivityDao.getAllActivitiesFlow().first()
            activities.map { it.activityType }.distinct().size
        }
    }

    // ===================== Utilities =====================

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getDateOnly(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Get usage for the last N days
     */
    fun getWeeklyUsage(): Flow<List<WaterActivity>> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis
        return waterActivityDao.getActivitiesBetween(startTime, endTime)
    }
}


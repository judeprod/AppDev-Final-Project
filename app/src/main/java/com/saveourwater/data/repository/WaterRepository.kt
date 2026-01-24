package com.saveourwater.data.repository

import com.saveourwater.data.local.dao.EcoGoalDao
import com.saveourwater.data.local.dao.WaterActivityDao
import com.saveourwater.data.local.entities.EcoGoal
import com.saveourwater.data.local.entities.WaterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Repository for Water data operations
 * PHASE2-FEAT-P0-015: Build WaterRepository
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

package com.saveourwater.ui.tracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saveourwater.R
import com.saveourwater.data.local.database.AppDatabase
import com.saveourwater.data.local.entities.ActivityType
import com.saveourwater.data.local.entities.WaterActivity
import com.saveourwater.data.repository.WaterRepository
import com.saveourwater.utils.WaterCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Tracking Fragment
 * PHASE2-FEAT-P0-014: Implement TrackingViewModel
 */
class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = WaterRepository(
        database.waterActivityDao(),
        database.ecoGoalDao()
    )

    // Today's usage as StateFlow (reactive)
    val todayUsage: StateFlow<Double> = repository.getTodayUsage()
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Daily goal
    private val _dailyGoal = MutableLiveData(150.0)
    val dailyGoal: LiveData<Double> = _dailyGoal

    // Activity items for the grid
    private val _activityItems = MutableLiveData<List<ActivityItem>>()
    val activityItems: LiveData<List<ActivityItem>> = _activityItems

    init {
        loadActivityItems()
        loadDailyGoal()
    }

    private fun loadActivityItems() {
        val items = listOf(
            ActivityItem(ActivityType.SHOWER, WaterCalculator.getActivityLabel(ActivityType.SHOWER), 
                WaterCalculator.getEstimatedUsageString(ActivityType.SHOWER), R.drawable.ic_shower),
            ActivityItem(ActivityType.TAP, WaterCalculator.getActivityLabel(ActivityType.TAP), 
                WaterCalculator.getEstimatedUsageString(ActivityType.TAP), R.drawable.ic_tap),
            ActivityItem(ActivityType.TOILET, WaterCalculator.getActivityLabel(ActivityType.TOILET), 
                WaterCalculator.getEstimatedUsageString(ActivityType.TOILET), R.drawable.ic_toilet),
            ActivityItem(ActivityType.LAUNDRY, WaterCalculator.getActivityLabel(ActivityType.LAUNDRY), 
                WaterCalculator.getEstimatedUsageString(ActivityType.LAUNDRY), R.drawable.ic_laundry),
            ActivityItem(ActivityType.DISHES, WaterCalculator.getActivityLabel(ActivityType.DISHES), 
                WaterCalculator.getEstimatedUsageString(ActivityType.DISHES), R.drawable.ic_dishes),
            ActivityItem(ActivityType.CUSTOM, WaterCalculator.getActivityLabel(ActivityType.CUSTOM), 
                "? L", R.drawable.ic_custom)
        )
        _activityItems.value = items
    }

    private fun loadDailyGoal() {
        viewModelScope.launch {
            val goal = repository.getActiveGoal()
            _dailyGoal.value = goal?.dailyLimitLiters ?: 150.0
        }
    }

    /**
     * Log a water activity using default duration
     */
    fun logQuickActivity(activityType: ActivityType) {
        viewModelScope.launch {
            val defaultDuration = WaterCalculator.getDefaultDuration(activityType)
            val liters = WaterCalculator.calculateLiters(activityType, defaultDuration)

            val activity = WaterActivity(
                activityType = activityType,
                litersUsed = liters,
                durationSeconds = defaultDuration * 60,
                notes = "Quick log"
            )

            repository.insertActivity(activity)
        }
    }

    /**
     * Log a water activity with custom parameters
     */
    fun logActivity(activityType: ActivityType, durationMinutes: Int, customLiters: Double? = null) {
        viewModelScope.launch {
            val liters = customLiters ?: WaterCalculator.calculateLiters(activityType, durationMinutes)

            val activity = WaterActivity(
                activityType = activityType,
                litersUsed = liters,
                durationSeconds = durationMinutes * 60,
                notes = "Manual entry"
            )

            repository.insertActivity(activity)
        }
    }

    /**
     * Calculate progress percentage
     */
    fun calculateProgress(usage: Double): Int {
        val goal = _dailyGoal.value ?: 150.0
        return ((usage / goal) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Log a water activity from the detailed tracking dialog
     * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
     */
    fun logDetailedActivity(estimatedLiters: Double) {
        viewModelScope.launch {
            val activity = WaterActivity(
                activityType = ActivityType.CUSTOM,
                litersUsed = estimatedLiters,
                durationSeconds = 0,
                notes = "Detailed tracking estimation"
            )
            repository.insertActivity(activity)
        }
    }
}

/**
 * Data class for activity grid items
 */
data class ActivityItem(
    val type: ActivityType,
    val name: String,
    val estimatedUsage: String,
    val iconRes: Int
)


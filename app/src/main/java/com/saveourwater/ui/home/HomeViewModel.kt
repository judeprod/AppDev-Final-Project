package com.saveourwater.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saveourwater.data.local.database.AppDatabase
import com.saveourwater.data.local.entities.WaterActivity
import com.saveourwater.data.repository.WaterRepository
import com.saveourwater.utils.WaterCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Home Dashboard
 * PHASE2-FEAT-P2-020: Implement HomeViewModel
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = WaterRepository(
        database.waterActivityDao(),
        database.ecoGoalDao()
    )

    // Today's usage
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

    // Current streak (placeholder - needs proper calculation)
    private val _currentStreak = MutableLiveData(0)
    val currentStreak: LiveData<Int> = _currentStreak

    // Recent activities
    val recentActivities: StateFlow<List<WaterActivity>> = repository.getRecentActivities(10)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Daily insight message
    private val _insightMessage = MutableLiveData<String>()
    val insightMessage: LiveData<String> = _insightMessage

    init {
        loadDailyGoal()
        loadStreak()
        generateInsight()
    }

    private fun loadDailyGoal() {
        viewModelScope.launch {
            val goal = repository.getActiveGoal()
            _dailyGoal.value = goal?.dailyLimitLiters ?: 150.0
        }
    }

    private fun loadStreak() {
        viewModelScope.launch {
            val daysLogged = repository.getDaysWithActivities()
            _currentStreak.value = daysLogged
        }
    }

    private fun generateInsight() {
        viewModelScope.launch {
            // Generate contextual insight based on usage
            val usage = todayUsage.value
            val goal = _dailyGoal.value ?: 150.0
            val percentage = (usage / goal * 100).toInt()

            _insightMessage.value = when {
                usage == 0.0 -> "💧 Start tracking your water usage today!"
                percentage >= 100 -> "⚠️ You've exceeded your daily goal. Try shorter showers tomorrow!"
                percentage >= 75 -> "🌊 Almost at your daily limit. Be mindful with remaining usage!"
                percentage >= 50 -> "👍 Good pace! You're at ${percentage}% of your daily goal."
                else -> WaterCalculator.getSavingsMessage(goal - usage)
            }
        }
    }

    /**
     * Calculate goal progress percentage
     */
    fun calculateProgress(usage: Double): Int {
        val goal = _dailyGoal.value ?: 150.0
        return ((usage / goal) * 100).toInt().coerceIn(0, 100)
    }
}

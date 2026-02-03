package com.saveourwater.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementCategory
import com.saveourwater.domain.AchievementManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Achievements screen
 * PHASE3-FEAT-P1-028: Create AchievementsViewModel
 */
class AchievementsViewModel(
    private val achievementManager: AchievementManager
) : ViewModel() {

    // ===================== UI State =====================
    
    private val _selectedCategory = MutableStateFlow<AchievementCategory?>(null)
    val selectedCategory: StateFlow<AchievementCategory?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // All achievements reactive stream
    private val allAchievements: Flow<List<Achievement>> = achievementManager.getAllAchievements()

    // Filtered achievements based on selected category
    val achievements: StateFlow<List<Achievement>> = combine(
        allAchievements,
        _selectedCategory
    ) { achievements, category ->
        _isLoading.value = false
        if (category == null) {
            achievements
        } else {
            achievements.filter { it.category == category }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Progress counting
    val unlockedCount: StateFlow<Int> = achievementManager.getUnlockedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = achievementManager.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Progress percentage for progress bar
    val progressPercentage: StateFlow<Int> = combine(unlockedCount, totalCount) { unlocked, total ->
        if (total == 0) 0 else (unlocked * 100 / total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ===================== Actions =====================

    fun selectCategory(category: AchievementCategory?) {
        _selectedCategory.value = category
    }

    fun refreshAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            achievementManager.checkAchievements()
            _isLoading.value = false
        }
    }

    // ===================== Factory =====================

    class Factory(
        private val achievementManager: AchievementManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
                return AchievementsViewModel(achievementManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

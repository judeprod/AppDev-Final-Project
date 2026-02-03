package com.saveourwater.data.local.dao

import androidx.room.*
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Achievement
 * PHASE3-FEAT-P0-024: Implement AchievementManager
 */
@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<Achievement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement)

    @Update
    suspend fun update(achievement: Achievement)

    @Query("SELECT * FROM achievements ORDER BY tier ASC, name ASC")
    fun getAllAchievementsFlow(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements ORDER BY tier ASC, name ASC")
    suspend fun getAllAchievements(): List<Achievement>

    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY tier ASC")
    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 0")
    suspend fun getLockedAchievements(): List<Achievement>

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getById(id: String): Achievement?

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM achievements")
    fun getTotalCount(): Flow<Int>
}

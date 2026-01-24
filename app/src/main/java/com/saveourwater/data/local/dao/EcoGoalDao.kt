package com.saveourwater.data.local.dao

import androidx.room.*
import com.saveourwater.data.local.entities.EcoGoal
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for EcoGoal
 * PHASE1-DB-P0-005: Create DAO Interfaces
 */
@Dao
interface EcoGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: EcoGoal): Long

    @Update
    suspend fun update(goal: EcoGoal)

    @Delete
    suspend fun delete(goal: EcoGoal)

    @Query("SELECT * FROM eco_goals WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveGoal(): EcoGoal?

    @Query("SELECT * FROM eco_goals WHERE isActive = 1 LIMIT 1")
    fun getActiveGoalFlow(): Flow<EcoGoal?>

    @Query("SELECT * FROM eco_goals ORDER BY startDate DESC")
    fun getAllGoals(): Flow<List<EcoGoal>>

    @Query("UPDATE eco_goals SET isActive = 0 WHERE id != :activeId")
    suspend fun deactivateOtherGoals(activeId: Long)
}

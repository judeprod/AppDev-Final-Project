package com.saveourwater.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * EcoGoal Entity - Stores user's daily water usage goals
 * PHASE1-DB-P0-004: Design Room Database Schema
 */
@Entity(tableName = "eco_goals")
data class EcoGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dailyLimitLiters: Double,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true
)

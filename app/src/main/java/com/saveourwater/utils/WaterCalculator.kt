package com.saveourwater.utils

import com.saveourwater.data.local.entities.ActivityType
import com.saveourwater.data.local.entities.FlowPressure
import com.saveourwater.data.local.entities.WaterSource

/**
 * Utility for water usage calculations
 * PHASE2-FEAT-P1-016: Create WaterCalculator Utility
 * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
 */
object WaterCalculator {

    /**
     * Continuity factors for Eco-Mode calculation
     * PHASE2-FEAT-P1-023: Enhanced Estimation Logic
     */
    const val CONTINUITY_CONTINUOUS = 1.0      // Water running continuously
    const val CONTINUITY_INTERMITTENT = 0.6    // Eco-mode: water off ~40% of time

    /**
     * Average liters per minute for each activity type
     * Based on typical household water usage data
     */
    private val USAGE_RATES = mapOf(
        ActivityType.SHOWER to 10.0,      // 10 L/min
        ActivityType.TAP to 7.0,          // 7 L/min
        ActivityType.TOILET to 9.0,       // 9 L per flush (instant)
        ActivityType.LAUNDRY to 65.0,     // 65 L per cycle
        ActivityType.DISHES to 25.0,      // 25 L per load (dishwasher)
        ActivityType.GARDEN to 15.0,      // 15 L/min (hose)
        ActivityType.CUSTOM to 0.0        // User-defined
    )

    /**
     * Default durations for quick-log (in minutes)
     */
    private val DEFAULT_DURATIONS = mapOf(
        ActivityType.SHOWER to 5,
        ActivityType.TAP to 1,
        ActivityType.TOILET to 0,    // Instant
        ActivityType.LAUNDRY to 0,   // Per cycle
        ActivityType.DISHES to 0,    // Per load
        ActivityType.GARDEN to 10,
        ActivityType.CUSTOM to 1
    )

    /**
     * Calculate estimated volume based on behavioral variables
     * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
     * 
     * Formula: (BaseFlowRate × PressureMultiplier × Duration) × ContinuityFactor
     * 
     * @param source Water source type (Shower or Bucket/Faucet)
     * @param pressure Flow pressure level (Low, Normal, High)
     * @param durationMinutes Duration of water usage in minutes
     * @param isIntermittent True if user turns off water while scrubbing (Eco-Mode)
     * @return Estimated water volume in liters
     */
    fun calculateEstimatedVolume(
        source: WaterSource,
        pressure: FlowPressure,
        durationMinutes: Int,
        isIntermittent: Boolean
    ): Double {
        val baseRate = source.baseFlowRate
        val pressureMultiplier = pressure.multiplier
        val continuityFactor = if (isIntermittent) CONTINUITY_INTERMITTENT else CONTINUITY_CONTINUOUS
        
        return (baseRate * pressureMultiplier * durationMinutes) * continuityFactor
    }

    /**
     * Calculate liters used based on activity type and duration
     */
    fun calculateLiters(activityType: ActivityType, minutes: Int): Double {
        val rate = USAGE_RATES[activityType] ?: 0.0
        return when (activityType) {
            // Fixed amount activities (not duration-based)
            ActivityType.TOILET, ActivityType.LAUNDRY, ActivityType.DISHES -> rate
            // Duration-based activities
            else -> rate * minutes
        }
    }

    /**
     * Get default duration for activity type
     */
    fun getDefaultDuration(activityType: ActivityType): Int {
        return DEFAULT_DURATIONS[activityType] ?: 1
    }

    /**
     * Get usage rate for activity type (L/min or L/use)
     */
    fun getUsageRate(activityType: ActivityType): Double {
        return USAGE_RATES[activityType] ?: 0.0
    }

    /**
     * Estimate duration from liters
     */
    fun estimateDuration(activityType: ActivityType, liters: Double): Int {
        val rate = USAGE_RATES[activityType] ?: return 0
        return when (activityType) {
            ActivityType.TOILET, ActivityType.LAUNDRY, ActivityType.DISHES -> 1
            else -> if (rate > 0) (liters / rate).toInt() else 0
        }
    }

    /**
     * Generate contextual savings message
     */
    fun getSavingsMessage(litersSaved: Double): String {
        return when {
            litersSaved >= 1000 -> "🌊 Enough water to irrigate a small garden for a week!"
            litersSaved >= 500 -> "💧 Daily water needs for a family of 4!"
            litersSaved >= 200 -> "🚿 That's 20 showers worth of water!"
            litersSaved >= 100 -> "🥤 Enough drinking water for one person for 50 days!"
            litersSaved >= 50 -> "🛁 A full bathtub!"
            litersSaved >= 20 -> "🚿 A 2-minute shower!"
            litersSaved >= 10 -> "🚿 A quick shower's worth!"
            else -> "💧 Every drop counts!"
        }
    }

    /**
     * Get display label for activity type
     */
    fun getActivityLabel(activityType: ActivityType): String {
        return when (activityType) {
            ActivityType.SHOWER -> "Shower"
            ActivityType.TAP -> "Tap"
            ActivityType.TOILET -> "Toilet"
            ActivityType.LAUNDRY -> "Laundry"
            ActivityType.DISHES -> "Dishes"
            ActivityType.GARDEN -> "Garden"
            ActivityType.CUSTOM -> "Custom"
        }
    }

    /**
     * Get estimated usage string for display
     */
    fun getEstimatedUsageString(activityType: ActivityType): String {
        val rate = USAGE_RATES[activityType] ?: 0.0
        val defaultDuration = DEFAULT_DURATIONS[activityType] ?: 1
        return when (activityType) {
            ActivityType.TOILET, ActivityType.LAUNDRY, ActivityType.DISHES -> 
                "~${rate.toInt()} L"
            else -> 
                "~${(rate * defaultDuration).toInt()} L"
        }
    }
}


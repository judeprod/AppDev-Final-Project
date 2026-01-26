package com.saveourwater.data.local.entities

/**
 * Enum representing water flow pressure levels
 * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
 * 
 * Pressure affects the flow rate multiplier:
 * - LOW: Reduces flow by 20% (0.8x)
 * - NORMAL: Standard flow (1.0x)
 * - HIGH: Increases flow by 50% (1.5x)
 */
enum class FlowPressure(val multiplier: Double, val displayName: String) {
    LOW(0.8, "Low"),
    NORMAL(1.0, "Normal"),
    HIGH(1.5, "High")
}

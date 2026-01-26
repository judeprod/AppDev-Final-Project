package com.saveourwater.data.local.entities

/**
 * Enum representing water source types for bathing activities
 * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
 * 
 * Different sources have different base flow rates:
 * - SHOWER: Higher flow rate (9 L/min)
 * - BUCKET_FAUCET: Lower, controlled flow (6 L/min)
 */
enum class WaterSource(val baseFlowRate: Double, val displayName: String, val emoji: String) {
    SHOWER(9.0, "Shower", "🚿"),
    BUCKET_FAUCET(6.0, "Bucket/Faucet", "🪣")
}

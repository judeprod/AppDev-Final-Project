package com.saveourwater.data.local.entities

/**
 * Sync status for local entities
 * Used for offline-first sync with Supabase
 */
enum class SyncStatus {
    /** Not yet synced to cloud */
    PENDING,
    
    /** Successfully synced to cloud */
    SYNCED,
    
    /** Conflict detected, needs resolution */
    CONFLICT,
    
    /** Deleted locally, waiting for cloud delete */
    DELETED
}

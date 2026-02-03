package com.saveourwater.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.Realtime

/**
 * Singleton Supabase client for cloud sync
 * Initialized with project credentials from Supabase Dashboard
 */
object SupabaseModule {
    
    // Supabase project credentials
    private const val SUPABASE_URL = "https://dfzuylrosjcotpibdtij.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRmenV5bHJvc2pjb3RwaWJkdGlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAwMzQ3MTEsImV4cCI6MjA4NTYxMDcxMX0.E1VvyiRzJWFyo4YRZYldnvKqDkp71qNoLwdjoK4EwXk"
    
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Realtime)
        }
    }
    
    // Helper to check if Supabase is configured
    val isConfigured: Boolean = true
}

// Backward compatibility alias
typealias SupabaseClient = SupabaseModule


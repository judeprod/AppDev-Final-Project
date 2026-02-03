# Supabase Hybrid Sync Setup Guide

This guide walks you through setting up Supabase for real-time cloud sync with your Save Our Water app.

---

## Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Android App   │────▶│  Room Database  │────▶│    Supabase    │
│                 │     │   (Local)       │     │   (Cloud)       │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                              │                        │
                              ▼                        ▼
                        Offline-first           Real-time sync
                        Fast queries            Multi-device
```

**Flow:**
1. User logs activity → Saves to Room (instant)
2. WorkManager syncs to Supabase in background
3. On app launch, fetch remote changes → Merge to Room

---

## Part 1: Supabase Project Setup

### Step 1: Create Supabase Account
1. Go to [supabase.com](https://supabase.com)
2. Sign up with GitHub/Email
3. Click **New Project**
4. Choose a name: `save-our-water`
5. Set a database password (save this!) `saveourwater123$`
6. Select region closest to you 
7. Wait ~2 minutes for project creation

### Step 2: Get API Credentials
1. Go to **Settings** → **API**
2. Copy these values:

| Key | Location | Example |
|-----|----------|---------|
| **Project URL** | Under "Project URL" | `https://dfzuylrosjcotpibdtij.supabase.co` |
| **anon/public key** | Under "Project API keys" | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRmenV5bHJvc2pjb3RwaWJkdGlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAwMzQ3MTEsImV4cCI6MjA4NTYxMDcxMX0.E1VvyiRzJWFyo4YRZYldnvKqDkp71qNoLwdjoK4EwXk` |

---

## Part 2: SQL Migration Script

Go to **SQL Editor** in Supabase dashboard and run this script:

```sql
-- ============================================
-- SAVE OUR WATER - SUPABASE SCHEMA
-- Version: 1.0.0
-- Run this in Supabase SQL Editor
-- ============================================

-- ==========================================
-- 1. PROFILES TABLE (extends Supabase auth.users)
-- ==========================================
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    display_name TEXT,
    avatar_url TEXT,
    daily_goal_liters DECIMAL DEFAULT 150.0,
    notification_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Auto-create profile on user signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email)
    VALUES (NEW.id, NEW.email);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ==========================================
-- 2. WATER ACTIVITIES TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS water_activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    
    -- Activity details
    activity_type TEXT NOT NULL CHECK (activity_type IN (
        'SHOWER', 'TAP', 'TOILET', 'LAUNDRY', 'DISHES', 'GARDEN'
    )),
    liters_used DECIMAL NOT NULL CHECK (liters_used >= 0),
    duration_minutes INTEGER,
    
    -- Estimation fields
    water_source TEXT CHECK (water_source IN ('SHOWER', 'BUCKET_FAUCET')),
    flow_pressure TEXT CHECK (flow_pressure IN ('LOW', 'NORMAL', 'HIGH')),
    is_eco_mode BOOLEAN DEFAULT false,
    
    -- Metadata
    notes TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT now(),
    
    -- Sync tracking
    local_id TEXT,  -- Maps to Room database ID
    synced_at TIMESTAMPTZ DEFAULT now(),
    
    -- Indexes for common queries
    CONSTRAINT valid_activity CHECK (liters_used > 0 OR duration_minutes > 0)
);

CREATE INDEX idx_activities_user_timestamp 
    ON water_activities(user_id, timestamp DESC);
CREATE INDEX idx_activities_local_id 
    ON water_activities(local_id);

-- ==========================================
-- 3. ECO GOALS TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS eco_goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    
    daily_limit_liters DECIMAL NOT NULL CHECK (daily_limit_liters > 0),
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN DEFAULT true,
    
    created_at TIMESTAMPTZ DEFAULT now(),
    local_id TEXT,
    synced_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_goals_user_active 
    ON eco_goals(user_id, is_active);

-- ==========================================
-- 4. ACHIEVEMENTS TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    
    achievement_id TEXT NOT NULL,  -- e.g., "streak_3_days"
    name TEXT NOT NULL,
    description TEXT,
    category TEXT CHECK (category IN ('STREAKS', 'CONSERVATION', 'BEHAVIORAL')),
    tier TEXT CHECK (tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM')),
    icon_name TEXT,
    
    target_value INTEGER NOT NULL DEFAULT 1,
    current_progress INTEGER NOT NULL DEFAULT 0,
    is_unlocked BOOLEAN DEFAULT false,
    unlocked_at TIMESTAMPTZ,
    
    synced_at TIMESTAMPTZ DEFAULT now(),
    
    UNIQUE(user_id, achievement_id)
);

CREATE INDEX idx_achievements_user 
    ON achievements(user_id);

-- ==========================================
-- 5. SYNC LOG TABLE (for conflict resolution)
-- ==========================================
CREATE TABLE IF NOT EXISTS sync_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    record_id TEXT NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT now(),
    device_id TEXT
);

CREATE INDEX idx_sync_log_user_time 
    ON sync_log(user_id, timestamp DESC);

-- ==========================================
-- 6. ROW LEVEL SECURITY (RLS)
-- ==========================================

-- Enable RLS on all tables
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE water_activities ENABLE ROW LEVEL SECURITY;
ALTER TABLE eco_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE achievements ENABLE ROW LEVEL SECURITY;
ALTER TABLE sync_log ENABLE ROW LEVEL SECURITY;

-- Profiles: Users can only see/edit their own profile
CREATE POLICY "Users can view own profile"
    ON profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
    ON profiles FOR UPDATE
    USING (auth.uid() = id);

-- Water Activities: Users can CRUD their own activities
CREATE POLICY "Users can view own activities"
    ON water_activities FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own activities"
    ON water_activities FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own activities"
    ON water_activities FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own activities"
    ON water_activities FOR DELETE
    USING (auth.uid() = user_id);

-- Eco Goals: Same pattern
CREATE POLICY "Users can view own goals"
    ON eco_goals FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own goals"
    ON eco_goals FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own goals"
    ON eco_goals FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own goals"
    ON eco_goals FOR DELETE
    USING (auth.uid() = user_id);

-- Achievements: Same pattern
CREATE POLICY "Users can view own achievements"
    ON achievements FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own achievements"
    ON achievements FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own achievements"
    ON achievements FOR UPDATE
    USING (auth.uid() = user_id);

-- Sync Log: Same pattern
CREATE POLICY "Users can view own sync log"
    ON sync_log FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own sync log"
    ON sync_log FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- ==========================================
-- 7. REAL-TIME SUBSCRIPTIONS
-- ==========================================

-- Enable real-time for specific tables
ALTER PUBLICATION supabase_realtime ADD TABLE water_activities;
ALTER PUBLICATION supabase_realtime ADD TABLE achievements;

-- ==========================================
-- 8. HELPER FUNCTIONS
-- ==========================================

-- Get user's current streak
CREATE OR REPLACE FUNCTION get_user_streak(p_user_id UUID)
RETURNS INTEGER AS $$
DECLARE
    streak_count INTEGER := 0;
    check_date DATE := CURRENT_DATE;
    has_activity BOOLEAN;
BEGIN
    LOOP
        SELECT EXISTS (
            SELECT 1 FROM water_activities 
            WHERE user_id = p_user_id 
            AND DATE(timestamp) = check_date
        ) INTO has_activity;
        
        IF has_activity THEN
            streak_count := streak_count + 1;
            check_date := check_date - 1;
        ELSE
            EXIT;
        END IF;
    END LOOP;
    
    RETURN streak_count;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Get user's daily usage summary
CREATE OR REPLACE FUNCTION get_daily_summary(
    p_user_id UUID,
    p_date DATE DEFAULT CURRENT_DATE
)
RETURNS TABLE (
    total_liters DECIMAL,
    activity_count INTEGER,
    eco_mode_count INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COALESCE(SUM(liters_used), 0)::DECIMAL,
        COUNT(*)::INTEGER,
        COUNT(*) FILTER (WHERE is_eco_mode)::INTEGER
    FROM water_activities
    WHERE user_id = p_user_id
    AND DATE(timestamp) = p_date;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ==========================================
-- SETUP COMPLETE!
-- ==========================================
```

---

## Part 3: Configure Android App

### Step 1: Add Supabase API Key

Edit `gradle.properties`:

```properties
# Replace with your actual Supabase values
supabase.url=https://YOUR-PROJECT-ID.supabase.co
supabase.anon.key=YOUR-ANON-KEY-HERE
```

### Step 2: Add Dependencies

Add to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Supabase Kotlin Client
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    
    // Ktor engine for Supabase
    implementation("io.ktor:ktor-client-android:3.1.1")
}
```

### Step 3: Create Supabase Client

Create `SupabaseClient.kt`:

```kotlin
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Realtime)
    }
}
```

---

## Part 4: Sync Strategy

### Sync Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    APP STARTUP                              │
├─────────────────────────────────────────────────────────────┤
│ 1. Check network connectivity                               │
│ 2. If online → Pull remote changes since last sync          │
│ 3. Merge remote → Room (conflict: latest timestamp wins)    │
│ 4. Push local unsync'd changes → Supabase                   │
│ 5. Mark all as synced                                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    USER LOGS ACTIVITY                       │
├─────────────────────────────────────────────────────────────┤
│ 1. Save to Room immediately (sync_status = PENDING)         │
│ 2. Schedule WorkManager sync job                            │
│ 3. WorkManager → Push to Supabase                           │
│ 4. On success → Update sync_status = SYNCED                 │
└─────────────────────────────────────────────────────────────┘
```

### Room Entity Changes

Add sync fields to existing entities:

```kotlin
@Entity(tableName = "water_activities")
data class WaterActivity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    // ... existing fields ...
    
    // Sync fields
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo(name = "last_modified") val lastModified: Long = System.currentTimeMillis()
)

enum class SyncStatus { PENDING, SYNCED, CONFLICT }
```

---

## Part 5: Testing

### Test Auth Flow
1. Create test user in Supabase Dashboard → Authentication → Users → Add User
2. Login with email/password in app
3. Verify profile auto-created

### Test Sync
1. Log activity while offline
2. Check Room: `sync_status = PENDING`
3. Go online
4. Check Supabase: activity should appear
5. Check Room: `sync_status = SYNCED`

---

## Cost Estimate (Free Tier)

| Resource | Limit | Save Our Water Usage |
|----------|-------|---------------------|
| Database | 500 MB | ~100K activities |
| Bandwidth | 5 GB/month | ~50K syncs/month |
| Auth | Unlimited | ✅ |
| Real-time | 200 concurrent | ✅ |

**Verdict:** Free tier is sufficient for class project and early users.

## Part 6: Implemented Android Files ✅

### Core Sync Files
| File | Purpose |
|------|---------|
| `data/remote/SupabaseClient.kt` | Singleton client with Postgrest, Auth, Realtime |
| `data/local/entities/SyncStatus.kt` | Enum: PENDING, SYNCED, CONFLICT, DELETED |
| `data/sync/SyncManager.kt` | Bidirectional sync logic (push pending, pull remote) |
| `data/sync/SyncWorker.kt` | WorkManager job for background sync |

### Authentication UI
| File | Purpose |
|------|---------|
| `ui/auth/AuthFragment.kt` | Login/signup screen with validation |
| `ui/auth/AuthViewModel.kt` | Auth state management with Supabase |
| `res/layout/fragment_auth.xml` | Material Design auth form |

### Updated Files
| File | Changes |
|------|---------|
| `gradle.properties` | Added `supabase.url` and `supabase.anon.key` |
| `app/build.gradle.kts` | Added Supabase SDK + Ktor dependencies |
| `WaterActivity.kt` | Added sync fields: `remoteId`, `syncStatus`, `lastModified` |
| `WaterActivityDao.kt` | Added `getById`, `getActivitiesBySyncStatus` |
| `Converters.kt` | Added `SyncStatus` type converter |
| `colors.xml` | Added semantic color aliases |
| `strings.xml` | Added Auth-related string resources |
| `nav_graph.xml` | Added AuthFragment with navigation actions |

### Icons Added
- `ic_email.xml` - Email input icon
- `ic_lock.xml` - Password input icon

---

## Next Steps Checklist

- [x] Create Supabase project
- [x] Run SQL migration script
- [x] Add API keys to gradle.properties
- [x] Implement SupabaseClient.kt
- [x] Add sync fields to Room entities
- [x] Implement SyncManager
- [x] Create SyncWorker (WorkManager)
- [x] Create Auth UI (login/signup)
- [ ] Enable Email auth in Supabase Dashboard → Authentication → Providers
- [ ] Create test user in Supabase
- [ ] Test offline → online sync flow
- [ ] Add "Sync Now" button in Settings
- [ ] Add login status indicator in Settings

---

## How to Test

### 1. Enable Auth in Supabase
1. Go to Supabase Dashboard → Authentication → Providers
2. Enable **Email** provider
3. Optionally enable **Google** or other social providers

### 2. Create Test User
1. Go to Authentication → Users → Add User
2. Enter test email and password
3. User will auto-verify (no email needed for testing)

### 3. Test in App
1. Run the app
2. Navigate to Auth screen (or add a "Login" button in Settings)
3. Sign up with test credentials
4. Log a water activity
5. Check Supabase Dashboard → Table Editor → water_activities

---

*Build Status: ✅ Verified (February 3, 2026)*


# Code Review: Phase 3 - Gamification & Engagement

> **Review Date:** 2026-02-02  
> **Timeframe:** 2026-01-31 to 2026-02-04  
> **Reviewer:** Development Team  
> **Status:** 🚧 In Progress  
> **Target Phase:** Phase 3 - Gamification & Engagement

---

## Executive Summary

Phase 3 implements the gamification and engagement features of the **Save Our Water** application. This phase builds upon the core tracking functionality (Phase 2) to add an achievement system, streak tracking, notifications, and social sharing capabilities to encourage water conservation habits.

### Key Objectives
- 🏆 **Achievement System** - Define and track user accomplishments
- 🔥 **Streak Tracking** - Calculate consecutive days of conservation
- 🔔 **Notifications** - Alert users when achievements are unlocked
- 📱 **Achievements UI** - Display achievements with filtering and detail views
- 📤 **Social Sharing** - Share achievements with friends

---

## Sprint Task Conventions

### Task Title Format
```
PHASE[X]-[CATEGORY]-[PRIORITY]-[ID]: Brief Description
```

**Example:** `PHASE3-FEAT-P0-023: Define Achievement System`

### Category Prefixes
| Prefix | Category | Description |
|--------|----------|-------------|
| `UI` | User Interface | Fragments, Activities, Layouts |
| `FEAT` | Feature Logic | ViewModels, business logic, managers |
| `DB` | Database | DAOs, Repositories, seeding |
| `UTIL` | Utilities | Helper classes, calculators |
| `RES` | Resources | Drawables, strings, themes |

### Priority Levels
| Priority | Meaning |
|----------|---------|
| `P0` | Critical - Must complete this sprint |
| `P1` | High - Should complete this sprint |
| `P2` | Medium - Nice to have this sprint |
| `P3` | Low - Backlog candidate |

---

## Proposed Sprint Backlog

### Achievement System Core
| Task ID | Title | Story Points | Status |
|---------|-------|--------------|--------|
| `PHASE3-FEAT-P0-023` | Define Achievement System | 5 | ⏳ |
| `PHASE3-FEAT-P0-024` | Implement AchievementManager | 5 | ⏳ |
| `PHASE3-DB-P1-025` | Seed Achievement Database | 3 | ⏳ |

#### PHASE3-FEAT-P0-023: Define Achievement System
**Description:**  
Create Achievement data class and AchievementDefinitions object with all predefined achievements (streaks, milestones, behavioral).

**Implementation Details:**

**Achievement.kt:**
```kotlin
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val iconResId: Int,
    val targetValue: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0
)

enum class AchievementCategory {
    STREAKS,        // Consecutive days
    CONSERVATION,   // Water saved milestones
    BEHAVIORAL      // Usage patterns
}

enum class AchievementTier {
    BRONZE,     // Easy achievements
    SILVER,     // Medium achievements
    GOLD,       // Hard achievements
    PLATINUM    // Expert achievements
}
```

**AchievementDefinitions.kt:**
```kotlin
object AchievementDefinitions {
    val ALL_ACHIEVEMENTS = listOf(
        // Streak Achievements
        Achievement(
            id = "streak_3_days",
            name = "Getting Started",
            description = "Log water usage for 3 consecutive days",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 3
        ),
        Achievement(
            id = "streak_7_days",
            name = "Week Warrior",
            description = "Maintain a 7-day tracking streak",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 7
        ),
        Achievement(
            id = "streak_30_days",
            name = "Monthly Master",
            description = "Achieve a 30-day tracking streak",
            category = AchievementCategory.STREAKS,
            tier = AchievementTier.GOLD,
            iconResId = R.drawable.ic_achievement_streak,
            targetValue = 30
        ),
        
        // Conservation Achievements
        Achievement(
            id = "save_100_liters",
            name = "Drop Saver",
            description = "Save 100 liters compared to your baseline",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 100
        ),
        Achievement(
            id = "save_500_liters",
            name = "Water Guardian",
            description = "Save 500 liters compared to your baseline",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 500
        ),
        Achievement(
            id = "save_1000_liters",
            name = "Conservation Hero",
            description = "Save 1000 liters compared to your baseline",
            category = AchievementCategory.CONSERVATION,
            tier = AchievementTier.GOLD,
            iconResId = R.drawable.ic_achievement_save,
            targetValue = 1000
        ),
        
        // Behavioral Achievements
        Achievement(
            id = "eco_mode_5",
            name = "Eco Starter",
            description = "Use Eco-Mode 5 times",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_eco,
            targetValue = 5
        ),
        Achievement(
            id = "under_goal_7",
            name = "Goal Crusher",
            description = "Stay under daily goal for 7 days",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_goal,
            targetValue = 7
        ),
        Achievement(
            id = "first_activity",
            name = "First Drop",
            description = "Log your first water activity",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.BRONZE,
            iconResId = R.drawable.ic_achievement_first,
            targetValue = 1
        ),
        Achievement(
            id = "all_activity_types",
            name = "Well Rounded",
            description = "Log all 6 activity types at least once",
            category = AchievementCategory.BEHAVIORAL,
            tier = AchievementTier.SILVER,
            iconResId = R.drawable.ic_achievement_variety,
            targetValue = 6
        )
    )
}
```

**Files Created:**
- `Achievement.kt` - Room entity with achievement data
- `AchievementCategory.kt` - Category enum
- `AchievementTier.kt` - Tier enum (Bronze, Silver, Gold, Platinum)
- `AchievementDefinitions.kt` - Predefined achievements object

**Acceptance Criteria:**
- [ ] Achievement entity created with all required fields
- [ ] AchievementTier enum defined (Bronze, Silver, Gold, Platinum)
- [ ] 10+ achievements predefined across 3 categories
- [ ] Icons assigned for each tier

---

#### PHASE3-FEAT-P0-024: Implement AchievementManager
**Description:**  
Build UseCase layer for checking achievement criteria, unlocking achievements, and triggering notifications.

**Implementation Details:**

**AchievementManager.kt:**
```kotlin
class AchievementManager(
    private val achievementDao: AchievementDao,
    private val waterRepository: WaterRepository,
    private val notificationHelper: NotificationHelper
) {
    suspend fun checkAchievements() {
        val achievements = achievementDao.getAllAchievements()
        
        achievements.filter { !it.isUnlocked }.forEach { achievement ->
            val progress = calculateProgress(achievement)
            
            if (progress >= achievement.targetValue) {
                unlockAchievement(achievement)
            } else {
                updateProgress(achievement, progress)
            }
        }
    }
    
    private suspend fun calculateProgress(achievement: Achievement): Int {
        return when (achievement.id) {
            "streak_3_days", "streak_7_days", "streak_30_days" -> 
                waterRepository.getCurrentStreak()
            "save_100_liters", "save_500_liters", "save_1000_liters" -> 
                waterRepository.getTotalSaved().toInt()
            "eco_mode_5" -> 
                waterRepository.getEcoModeUsageCount()
            "under_goal_7" -> 
                waterRepository.getDaysUnderGoal()
            "first_activity" -> 
                if (waterRepository.getTotalActivities() > 0) 1 else 0
            "all_activity_types" -> 
                waterRepository.getUniqueActivityTypesLogged()
            else -> 0
        }
    }
    
    private suspend fun unlockAchievement(achievement: Achievement) {
        val unlocked = achievement.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis(),
            progress = achievement.targetValue
        )
        achievementDao.update(unlocked)
        notificationHelper.showAchievementUnlocked(unlocked)
    }
    
    private suspend fun updateProgress(achievement: Achievement, progress: Int) {
        achievementDao.update(achievement.copy(progress = progress))
    }
}
```

**Files Created:**
- `AchievementManager.kt` - UseCase for achievement logic
- `AchievementDao.kt` - Data Access Object for achievements

**Acceptance Criteria:**
- [ ] AchievementManager class created
- [ ] checkAchievements() method iterates all locked achievements
- [ ] calculateProgress() returns current progress for each type
- [ ] unlockAchievement() updates database and triggers notification

---

#### PHASE3-DB-P1-025: Seed Achievement Database
**Description:**  
Create database prepopulation logic to insert all predefined achievements on first app launch.

**Implementation Details:**

**AppDatabase.kt (modified):**
```kotlin
@Database(
    entities = [WaterActivity::class, EcoGoal::class, Achievement::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterActivityDao(): WaterActivityDao
    abstract fun ecoGoalDao(): EcoGoalDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "save_our_water_database"
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private class DatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed achievements on first install
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).achievementDao().insertAll(
                    AchievementDefinitions.ALL_ACHIEVEMENTS
                )
            }
        }
    }
}
```

**AchievementDao.kt:**
```kotlin
@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<Achievement>)
    
    @Query("SELECT * FROM achievements ORDER BY tier ASC")
    fun getAllAchievementsFlow(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE category = :category")
    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    @Update
    suspend fun update(achievement: Achievement)
    
    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getById(id: String): Achievement?
}
```

**Acceptance Criteria:**
- [ ] Database callback for seeding implemented
- [ ] All 10+ achievements inserted on first install
- [ ] Seeding runs only on first install (onCreate)
- [ ] Verified via Android Studio Database Inspector

---

### Achievements UI
| Task ID | Title | Story Points | Status |
|---------|-------|--------------|--------|
| `PHASE3-UI-P0-026` | Design Achievements Fragment Layout | 5 | ⏳ |
| `PHASE3-UI-P1-027` | Build Achievement Card Layout | 3 | ⏳ |
| `PHASE3-FEAT-P1-028` | Create AchievementsViewModel | 5 | ⏳ |
| `PHASE3-UI-P1-029` | Implement Achievement Adapter | 3 | ⏳ |

#### PHASE3-UI-P0-026: Design Achievements Fragment Layout
**Description:**  
Create fragment_achievements.xml with TabLayout for filtering and RecyclerView grid for achievement cards.

**Implementation Details:**

**fragment_achievements.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_light">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <!-- Header -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/achievements_title"
            android:textAppearance="@style/TextAppearance.Material3.HeadlineMedium"
            android:padding="16dp" />

        <!-- Tab Layout for filtering -->
        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:tabMode="scrollable"
            app:tabGravity="start"
            app:tabIndicatorColor="@color/primary_500"
            app:tabSelectedTextColor="@color/primary_500"
            app:tabTextColor="@color/gray_500">

            <com.google.android.material.tabs.TabItem
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/tab_all" />
            <com.google.android.material.tabs.TabItem
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/tab_streaks" />
            <com.google.android.material.tabs.TabItem
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/tab_conservation" />
            <com.google.android.material.tabs.TabItem
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/tab_behavioral" />
        </com.google.android.material.tabs.TabLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <!-- Loading Indicator -->
    <com.google.android.material.progressindicator.CircularProgressIndicator
        android:id="@+id/progressLoading"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:visibility="gone" />

    <!-- Achievement Grid -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvAchievements"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="8dp"
        android:clipToPadding="false"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
        app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
        app:spanCount="2" />

    <!-- Empty State -->
    <LinearLayout
        android:id="@+id/emptyState"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">

        <ImageView
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:src="@drawable/ic_achievements"
            android:alpha="0.5" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/achievements_empty"
            android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
            android:textColor="@color/gray_500"
            android:layout_marginTop="16dp" />
    </LinearLayout>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Acceptance Criteria:**
- [ ] TabLayout with 4 tabs (All, Streaks, Conservation, Behavioral)
- [ ] RecyclerView with GridLayoutManager (2 columns)
- [ ] Empty state view with icon and message
- [ ] Loading indicator (CircularProgressIndicator)

---

#### PHASE3-UI-P1-027: Build Achievement Card Layout
**Description:**  
Design item_achievement.xml with icon, name, tier badge, progress bar for locked achievements.

**Implementation Details:**

**item_achievement.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    android:clickable="true"
    android:focusable="true"
    android:foreground="?attr/selectableItemBackground">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp"
        android:gravity="center">

        <!-- Achievement Icon -->
        <ImageView
            android:id="@+id/ivAchievementIcon"
            android:layout_width="64dp"
            android:layout_height="64dp"
            android:src="@drawable/ic_achievement_default"
            android:contentDescription="@string/achievement_icon" />

        <!-- Achievement Name -->
        <TextView
            android:id="@+id/tvAchievementName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Achievement Name"
            android:textAppearance="@style/TextAppearance.Material3.TitleMedium"
            android:textAlignment="center"
            android:maxLines="2"
            android:ellipsize="end"
            android:layout_marginTop="8dp" />

        <!-- Achievement Description -->
        <TextView
            android:id="@+id/tvAchievementDescription"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Achievement description"
            android:textAppearance="@style/TextAppearance.Material3.BodySmall"
            android:textColor="@color/gray_500"
            android:textAlignment="center"
            android:maxLines="2"
            android:ellipsize="end"
            android:layout_marginTop="4dp" />

        <!-- Tier Chip -->
        <com.google.android.material.chip.Chip
            android:id="@+id/chipTier"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Bronze"
            app:chipMinHeight="24dp"
            app:textStartPadding="8dp"
            app:textEndPadding="8dp"
            android:layout_marginTop="8dp" />

        <!-- Progress Bar (for locked achievements) -->
        <com.google.android.material.progressindicator.LinearProgressIndicator
            android:id="@+id/progressAchievement"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            app:trackCornerRadius="4dp"
            app:trackThickness="8dp" />

        <!-- Progress Text -->
        <TextView
            android:id="@+id/tvProgress"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="3/7 days"
            android:textAppearance="@style/TextAppearance.Material3.LabelSmall"
            android:textColor="@color/gray_600"
            android:layout_marginTop="4dp" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

**Acceptance Criteria:**
- [ ] MaterialCardView with 16dp corner radius and 4dp elevation
- [ ] ImageView for achievement icon (64x64dp)
- [ ] TextView for name and description
- [ ] Chip for tier display (Bronze/Silver/Gold/Platinum)
- [ ] LinearProgressIndicator for partial completion

---

#### PHASE3-FEAT-P1-028: Create AchievementsViewModel
**Description:**  
Implement ViewModel for achievements screen with filtering logic, unlock state tracking, and progress calculation.

**Implementation Details:**

**AchievementsViewModel.kt:**
```kotlin
class AchievementsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val achievementDao = database.achievementDao()
    private val waterRepository = WaterRepository(
        database.waterActivityDao(),
        database.ecoGoalDao()
    )
    
    // All achievements from database
    private val allAchievements: StateFlow<List<Achievement>> = 
        achievementDao.getAllAchievementsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    // Selected filter category
    private val _selectedCategory = MutableStateFlow<AchievementCategory?>(null)
    val selectedCategory: StateFlow<AchievementCategory?> = _selectedCategory
    
    // Filtered achievements based on selected tab
    val filteredAchievements: StateFlow<List<Achievement>> = combine(
        allAchievements,
        _selectedCategory
    ) { achievements, category ->
        when (category) {
            null -> achievements
            else -> achievements.filter { it.category == category }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Loading state
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Stats
    val unlockedCount: StateFlow<Int> = allAchievements
        .map { list -> list.count { it.isUnlocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val totalCount: StateFlow<Int> = allAchievements
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    init {
        viewModelScope.launch {
            allAchievements.collect { 
                _isLoading.value = false
            }
        }
    }
    
    fun setFilter(category: AchievementCategory?) {
        _selectedCategory.value = category
    }
    
    fun onTabSelected(position: Int) {
        _selectedCategory.value = when (position) {
            0 -> null // All
            1 -> AchievementCategory.STREAKS
            2 -> AchievementCategory.CONSERVATION
            3 -> AchievementCategory.BEHAVIORAL
            else -> null
        }
    }
    
    fun getProgressPercentage(achievement: Achievement): Int {
        if (achievement.isUnlocked) return 100
        return ((achievement.progress.toFloat() / achievement.targetValue) * 100).toInt()
            .coerceIn(0, 100)
    }
}
```

**Acceptance Criteria:**
- [ ] AchievementsViewModel created with AndroidViewModel
- [ ] StateFlow for filtered achievements list
- [ ] Filter by category via setFilter() / onTabSelected()
- [ ] getProgressPercentage() for locked items

---

#### PHASE3-UI-P1-029: Implement Achievement Adapter
**Description:**  
Build RecyclerView adapter with locked/unlocked states, grayscale filter for locked items, and click animations.

**Implementation Details:**

**AchievementsAdapter.kt:**
```kotlin
class AchievementsAdapter(
    private val onAchievementClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementsAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position), onAchievementClick)
    }

    class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(achievement: Achievement, onClick: (Achievement) -> Unit) {
            binding.apply {
                tvAchievementName.text = achievement.name
                tvAchievementDescription.text = achievement.description
                ivAchievementIcon.setImageResource(achievement.iconResId)
                
                // Tier chip styling
                chipTier.text = achievement.tier.name
                chipTier.setChipBackgroundColorResource(getTierColor(achievement.tier))
                
                // Handle locked/unlocked state
                if (achievement.isUnlocked) {
                    showUnlockedState()
                } else {
                    showLockedState(achievement)
                }
                
                // Click animation
                root.setOnClickListener { view ->
                    view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .withEndAction {
                            view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                            onClick(achievement)
                        }
                        .start()
                }
            }
        }
        
        private fun showUnlockedState() {
            binding.apply {
                ivAchievementIcon.clearColorFilter()
                ivAchievementIcon.alpha = 1.0f
                progressAchievement.visibility = View.GONE
                tvProgress.visibility = View.GONE
                root.alpha = 1.0f
            }
        }
        
        private fun showLockedState(achievement: Achievement) {
            binding.apply {
                // Grayscale filter for locked icon
                val matrix = ColorMatrix().apply { setSaturation(0f) }
                ivAchievementIcon.colorFilter = ColorMatrixColorFilter(matrix)
                ivAchievementIcon.alpha = 0.5f
                root.alpha = 0.8f
                
                // Show progress
                progressAchievement.visibility = View.VISIBLE
                tvProgress.visibility = View.VISIBLE
                progressAchievement.progress = 
                    ((achievement.progress.toFloat() / achievement.targetValue) * 100).toInt()
                tvProgress.text = "${achievement.progress}/${achievement.targetValue}"
            }
        }
        
        private fun getTierColor(tier: AchievementTier): Int {
            return when (tier) {
                AchievementTier.BRONZE -> R.color.bronze
                AchievementTier.SILVER -> R.color.silver
                AchievementTier.GOLD -> R.color.gold
                AchievementTier.PLATINUM -> R.color.platinum
            }
        }
    }
    
    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement) = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement) = 
            oldItem == newItem
    }
}
```

**Acceptance Criteria:**
- [ ] ListAdapter with DiffUtil for efficient updates
- [ ] ViewHolder binding with locked/unlocked states
- [ ] Locked state styling (grayscale icon, 50% alpha)
- [ ] Item click animation (scale effect 0.95x)

---

### Notifications & Streaks
| Task ID | Title | Story Points | Status |
|---------|-------|--------------|--------|
| `PHASE3-FEAT-P1-030` | Build Achievement Notification System | 3 | ⏳ |
| `PHASE3-FEAT-P2-032` | Implement Streak Calculation | 3 | ⏳ |
| `PHASE3-UI-P2-033` | Add Streak Widget to Home | 2 | ⏳ |

#### PHASE3-FEAT-P1-030: Build Achievement Notification System
**Description:**  
Implement NotificationManager wrapper to display push notifications when achievements are unlocked.

**Implementation Details:**

**NotificationHelper.kt:**
```kotlin
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ACHIEVEMENTS = "achievements_channel"
        const val NOTIFICATION_ID_ACHIEVEMENT = 1001
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Achievement Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for unlocked achievements"
                enableVibration(true)
                enableLights(true)
                lightColor = Color.BLUE
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showAchievementUnlocked(achievement: Achievement) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "achievements")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_achievements)
            .setContentTitle("🏆 Achievement Unlocked!")
            .setContentText(achievement.name)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${achievement.name}\n${achievement.description}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_achievements,
                "View Achievements",
                pendingIntent
            )
            .build()
        
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
            == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(
                NOTIFICATION_ID_ACHIEVEMENT + achievement.id.hashCode(),
                notification
            )
        }
    }
}
```

**Acceptance Criteria:**
- [ ] Notification channel created (achievements_channel)
- [ ] showAchievementUnlocked() displays rich notification
- [ ] Custom notification layout with achievement name/description
- [ ] Action button to navigate to achievements screen

---

#### PHASE3-FEAT-P2-032: Implement Streak Calculation
**Description:**  
Add repository methods to calculate current conservation streak based on consecutive days with logged activities.

**Implementation Details:**

**WaterRepository.kt (additions):**
```kotlin
class WaterRepository(
    private val waterActivityDao: WaterActivityDao,
    private val ecoGoalDao: EcoGoalDao
) {
    // ... existing methods ...
    
    suspend fun getCurrentStreak(): Int {
        return withContext(Dispatchers.IO) {
            val activities = waterActivityDao.getAllActivitiesSortedByDate()
            calculateStreak(activities)
        }
    }
    
    private fun calculateStreak(activities: List<WaterActivity>): Int {
        if (activities.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        val today = getDateOnly(calendar.timeInMillis)
        
        // Get unique dates with activities
        val datesWithActivities = activities
            .map { getDateOnly(it.timestamp) }
            .distinct()
            .sortedDescending()
        
        if (datesWithActivities.isEmpty()) return 0
        
        // Check if today or yesterday has activity
        val latestDate = datesWithActivities.first()
        val yesterday = getDateOnly(today - 24 * 60 * 60 * 1000)
        
        if (latestDate != today && latestDate != yesterday) {
            return 0 // Streak broken
        }
        
        var streak = 0
        var expectedDate = if (latestDate == today) today else yesterday
        
        for (date in datesWithActivities) {
            if (date == expectedDate) {
                streak++
                expectedDate = getDateOnly(expectedDate - 24 * 60 * 60 * 1000)
            } else if (date < expectedDate) {
                break // Gap in days, streak ends
            }
        }
        
        return streak
    }
    
    private fun getDateOnly(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    suspend fun getTotalActivities(): Int {
        return withContext(Dispatchers.IO) {
            waterActivityDao.getTotalCount()
        }
    }
    
    suspend fun getUniqueActivityTypesLogged(): Int {
        return withContext(Dispatchers.IO) {
            waterActivityDao.getUniqueActivityTypes()
        }
    }
}
```

**Acceptance Criteria:**
- [ ] getCurrentStreak() function returns current streak days
- [ ] Correctly checks consecutive days
- [ ] Handles gaps correctly (streak resets)
- [ ] Returns Int (number of days)

---

#### PHASE3-UI-P2-033: Add Streak Widget to Home
**Description:**  
Create streak display widget on home screen showing current streak with fire icon and motivational message.

**Implementation Details:**

**fragment_home.xml (addition):**
```xml
<!-- Streak Card -->
<com.google.android.material.card.MaterialCardView
    android:id="@+id/cardStreak"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:layout_margin="4dp"
    app:cardCornerRadius="16dp"
    app:cardBackgroundColor="@color/accent_50"
    android:clickable="true"
    android:focusable="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp"
        android:gravity="center">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="🔥 Streak"
            android:textAppearance="@style/TextAppearance.Material3.LabelMedium"
            android:textColor="@color/gray_600" />

        <TextView
            android:id="@+id/tvStreakCount"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="7 days"
            android:textAppearance="@style/TextAppearance.Material3.HeadlineSmall"
            android:textColor="@color/accent_700"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvStreakMessage"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Keep it up!"
            android:textAppearance="@style/TextAppearance.Material3.LabelSmall"
            android:textColor="@color/gray_500" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

**HomeFragment.kt (addition):**
```kotlin
private fun observeStreak() {
    viewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
        binding.tvStreakCount.text = "$streak days"
        binding.tvStreakMessage.text = getStreakMessage(streak)
    }
    
    binding.cardStreak.setOnClickListener {
        // Navigate to streak history or achievements
        findNavController().navigate(R.id.nav_achievements)
    }
}

private fun getStreakMessage(streak: Int): String {
    return when {
        streak == 0 -> "Start tracking today!"
        streak < 3 -> "Great start!"
        streak < 7 -> "Keep it up!"
        streak < 14 -> "You're on fire! 🔥"
        streak < 30 -> "Amazing dedication!"
        else -> "Legendary! 🏆"
    }
}
```

**Acceptance Criteria:**
- [ ] Streak card on home screen with 🔥 icon
- [ ] "X days streak!" message displayed
- [ ] Motivational message based on streak length
- [ ] Tap to navigate to achievements screen

---

### Advanced Features
| Task ID | Title | Story Points | Status |
|---------|-------|--------------|--------|
| `PHASE3-UI-P2-031` | Create Achievement Detail Dialog | 3 | ⏳ |
| `PHASE3-FEAT-P3-034` | Create Share Achievement Feature | 3 | ⏳ |

#### PHASE3-UI-P2-031: Create Achievement Detail Dialog
**Description:**  
Build BottomSheetDialog to show full achievement details including description, unlock date, and share button.

**Implementation Details:**

**AchievementDetailBottomSheet.kt:**
```kotlin
class AchievementDetailBottomSheet : BottomSheetDialogFragment() {
    
    private var _binding: DialogAchievementDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var achievement: Achievement
    
    companion object {
        private const val ARG_ACHIEVEMENT_ID = "achievement_id"
        
        fun newInstance(achievementId: String): AchievementDetailBottomSheet {
            return AchievementDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_ACHIEVEMENT_ID, achievementId)
                }
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAchievementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val achievementId = arguments?.getString(ARG_ACHIEVEMENT_ID)
        // Load achievement from database and display
        
        binding.apply {
            tvAchievementName.text = achievement.name
            tvAchievementDescription.text = achievement.description
            ivAchievementIcon.setImageResource(achievement.iconResId)
            chipTier.text = achievement.tier.name
            
            if (achievement.isUnlocked) {
                tvUnlockDate.text = "Unlocked: ${formatDate(achievement.unlockedAt!!)}"
                tvUnlockDate.visibility = View.VISIBLE
                btnShare.visibility = View.VISIBLE
            } else {
                tvUnlockDate.visibility = View.GONE
                btnShare.visibility = View.GONE
                tvProgress.text = "Progress: ${achievement.progress}/${achievement.targetValue}"
            }
            
            btnShare.setOnClickListener {
                shareAchievement(achievement)
            }
            
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    private fun shareAchievement(achievement: Achievement) {
        // Will be implemented in PHASE3-FEAT-P3-034
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

**dialog_achievement_detail.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="24dp">

    <!-- Handle bar -->
    <View
        android:layout_width="40dp"
        android:layout_height="4dp"
        android:layout_gravity="center_horizontal"
        android:background="@drawable/bg_handle_bar" />

    <!-- Achievement Icon -->
    <ImageView
        android:id="@+id/ivAchievementIcon"
        android:layout_width="96dp"
        android:layout_height="96dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="24dp" />

    <!-- Achievement Name -->
    <TextView
        android:id="@+id/tvAchievementName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textAppearance="@style/TextAppearance.Material3.HeadlineSmall"
        android:layout_marginTop="16dp" />

    <!-- Tier Chip -->
    <com.google.android.material.chip.Chip
        android:id="@+id/chipTier"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="8dp" />

    <!-- Description -->
    <TextView
        android:id="@+id/tvAchievementDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
        android:textAlignment="center"
        android:textColor="@color/gray_600"
        android:layout_marginTop="16dp" />

    <!-- Unlock Date -->
    <TextView
        android:id="@+id/tvUnlockDate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textAppearance="@style/TextAppearance.Material3.LabelMedium"
        android:textColor="@color/accent_600"
        android:layout_marginTop="12dp" />

    <!-- Progress (for locked) -->
    <TextView
        android:id="@+id/tvProgress"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textAppearance="@style/TextAppearance.Material3.LabelMedium"
        android:textColor="@color/gray_500"
        android:layout_marginTop="12dp"
        android:visibility="gone" />

    <!-- Action Buttons -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="24dp"
        android:gravity="center">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnShare"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/share"
            app:icon="@drawable/ic_share"
            style="@style/Widget.Material3.Button.TonalButton" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnClose"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/close"
            android:layout_marginStart="16dp"
            style="@style/Widget.Material3.Button.OutlinedButton" />
    </LinearLayout>
</LinearLayout>
```

**Acceptance Criteria:**
- [ ] BottomSheetDialogFragment created
- [ ] Achievement details displayed (name, description, tier, icon)
- [ ] Unlock timestamp formatted ("MMM dd, yyyy")
- [ ] Share button visible for unlocked achievements

---

#### PHASE3-FEAT-P3-034: Create Share Achievement Feature
**Description:**  
Implement functionality to generate achievement image and share via Android ShareSheet.

**Implementation Details:**

**ShareHelper.kt:**
```kotlin
object ShareHelper {
    
    fun shareAchievement(context: Context, achievement: Achievement) {
        // Generate bitmap from achievement card
        val bitmap = generateAchievementBitmap(context, achievement)
        
        // Save bitmap to cache directory
        val file = saveBitmapToCache(context, bitmap, "achievement_${achievement.id}.png")
        
        // Get content URI using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                Intent.EXTRA_TEXT,
                "🏆 I just unlocked the \"${achievement.name}\" achievement in Save Our Water! " +
                "Join me in saving water: https://saveourwater.app"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
    }
    
    private fun generateAchievementBitmap(context: Context, achievement: Achievement): Bitmap {
        // Create a view for the achievement card
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.share_achievement_card, null)
        
        // Populate view with achievement data
        view.findViewById<ImageView>(R.id.ivAchievementIcon)
            .setImageResource(achievement.iconResId)
        view.findViewById<TextView>(R.id.tvAchievementName).text = achievement.name
        view.findViewById<TextView>(R.id.tvAchievementDescription).text = achievement.description
        view.findViewById<Chip>(R.id.chipTier).text = achievement.tier.name
        
        // Add app branding
        view.findViewById<ImageView>(R.id.ivAppLogo)
            .setImageResource(R.drawable.ic_water_drop)
        view.findViewById<TextView>(R.id.tvAppName).text = "Save Our Water"
        
        // Measure and layout the view
        view.measure(
            View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        
        // Create bitmap
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, 
            view.measuredHeight, 
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        
        return bitmap
    }
    
    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, filename: String): File {
        val cacheDir = File(context.cacheDir, "shared_images")
        cacheDir.mkdirs()
        
        val file = File(cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        return file
    }
}
```

**share_achievement_card.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="24dp"
    android:background="@drawable/bg_share_card">

    <!-- App Branding -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <ImageView
            android:id="@+id/ivAppLogo"
            android:layout_width="32dp"
            android:layout_height="32dp"
            android:src="@drawable/ic_water_drop" />

        <TextView
            android:id="@+id/tvAppName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Save Our Water"
            android:textAppearance="@style/TextAppearance.Material3.TitleMedium"
            android:textColor="@color/primary_700"
            android:layout_marginStart="8dp" />
    </LinearLayout>

    <!-- Achievement Icon -->
    <ImageView
        android:id="@+id/ivAchievementIcon"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="24dp" />

    <!-- Achievement Name -->
    <TextView
        android:id="@+id/tvAchievementName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:textAppearance="@style/TextAppearance.Material3.TitleLarge"
        android:layout_marginTop="16dp" />

    <!-- Tier Chip -->
    <com.google.android.material.chip.Chip
        android:id="@+id/chipTier"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="8dp" />

    <!-- Description -->
    <TextView
        android:id="@+id/tvAchievementDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
        android:textAlignment="center"
        android:textColor="@color/gray_600"
        android:layout_marginTop="12dp" />

    <!-- Call to Action -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:text="Join me in saving water! 💧"
        android:textAppearance="@style/TextAppearance.Material3.LabelLarge"
        android:textColor="@color/primary_500"
        android:layout_marginTop="16dp" />
</LinearLayout>
```

**Acceptance Criteria:**
- [ ] Generate bitmap from achievement card layout
- [ ] Include app branding (logo + name)
- [ ] Create shareable file URI using FileProvider
- [ ] Launch Android ShareSheet with image + text

---

## Resources to Create

### Kotlin Files (12)
| File | Purpose | Priority |
|------|---------|----------|
| `Achievement.kt` | Room entity | P0 |
| `AchievementCategory.kt` | Category enum | P0 |
| `AchievementTier.kt` | Tier enum | P0 |
| `AchievementDefinitions.kt` | Predefined achievements | P0 |
| `AchievementDao.kt` | Data Access Object | P0 |
| `AchievementManager.kt` | UseCase for achievement logic | P0 |
| `AchievementsFragment.kt` | Achievements screen | P0 |
| `AchievementsViewModel.kt` | ViewModel | P1 |
| `AchievementsAdapter.kt` | RecyclerView adapter | P1 |
| `NotificationHelper.kt` | Notification manager | P1 |
| `AchievementDetailBottomSheet.kt` | Detail dialog | P2 |
| `ShareHelper.kt` | Share functionality | P3 |

### XML Layouts (5)
| File | Purpose | Priority |
|------|---------|----------|
| `fragment_achievements.xml` | Achievements screen | P0 |
| `item_achievement.xml` | Achievement card | P1 |
| `dialog_achievement_detail.xml` | Detail bottom sheet | P2 |
| `share_achievement_card.xml` | Share image layout | P3 |
| `bg_share_card.xml` | Share card background | P3 |

### Vector Drawables (6)
| Icon | Purpose |
|------|---------|
| `ic_achievement_default.xml` | Default achievement icon |
| `ic_achievement_streak.xml` | Streak achievements |
| `ic_achievement_save.xml` | Conservation achievements |
| `ic_achievement_eco.xml` | Eco-mode achievements |
| `ic_achievement_goal.xml` | Goal achievements |
| `ic_achievement_variety.xml` | Variety achievements |

---

## Acceptance Criteria

### Phase 3 Completion Checklist
- [ ] **Achievement System**
  - [ ] Achievement entity with all fields
  - [ ] 10+ achievements predefined
  - [ ] AchievementManager with unlock logic
  - [ ] Database seeding on first install

- [ ] **Achievements UI**
  - [ ] Fragment with TabLayout and RecyclerView
  - [ ] Achievement cards with tier badges
  - [ ] Locked/unlocked states with proper styling
  - [ ] Detail bottom sheet dialog

- [ ] **Notifications**
  - [ ] Notification channel created
  - [ ] Achievement unlock notifications
  - [ ] Action to navigate to achievements

- [ ] **Streaks**
  - [ ] getCurrentStreak() calculation
  - [ ] Streak widget on home screen
  - [ ] Motivational messages

- [ ] **Sharing**
  - [ ] Achievement image generation
  - [ ] ShareSheet integration
  - [ ] App branding included

---

## Next Steps

### Phase 4 Preparation
1. ⏳ **Insights Engine** - Build insights generation logic
2. ⏳ **Educational Content** - Create water facts database
3. ⏳ **Impact Calculator** - Project savings from behavioral changes
4. ⏳ **InsightsFragment UI** - Design insights screen

### Technical Debt
- 🔄 Add unit tests for AchievementManager
- 🔄 Implement proper error handling for notifications
- 🔄 Add accessibility labels for achievement cards
- 🔄 Optimize bitmap generation for sharing

---

## Review Summary

**Total Tasks:** 12  
**Completed:** 0 (0%)  
**Total Story Points:** 43  
**Target Duration:** 5 days (Jan 31 - Feb 4, 2026)  
**Target Velocity:** 8.6 points/day

---

**Phase 3 Status: 🚧 IN PROGRESS**  
**Ready for Phase 4:** NO  
**Next Review:** Phase 4 - Value Awareness & Insights

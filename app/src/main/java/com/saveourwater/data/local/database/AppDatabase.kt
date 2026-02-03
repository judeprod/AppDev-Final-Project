package com.saveourwater.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saveourwater.data.AchievementDefinitions
import com.saveourwater.data.local.dao.AchievementDao
import com.saveourwater.data.local.dao.EcoGoalDao
import com.saveourwater.data.local.dao.WaterActivityDao
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.EcoGoal
import com.saveourwater.data.local.entities.WaterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database for Save Our Water app
 * PHASE1-DB-P0-006: Build AppDatabase Class
 * PHASE3-DB-P1-025: Added Achievement entity and seeding
 * Supabase Sync: v3 - Added sync fields to WaterActivity
 */
@Database(
    entities = [
        WaterActivity::class,
        EcoGoal::class,
        Achievement::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun waterActivityDao(): WaterActivityDao
    abstract fun ecoGoalDao(): EcoGoalDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "save_our_water_database"
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Database callback for seeding achievements on first install
     * PHASE3-DB-P1-025: Seed Achievement Database
     */
    private class DatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed achievements on first install
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.achievementDao().insertAll(
                        AchievementDefinitions.ALL_ACHIEVEMENTS
                    )
                }
            }
        }
    }
}

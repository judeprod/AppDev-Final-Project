package com.saveourwater.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saveourwater.data.local.dao.EcoGoalDao
import com.saveourwater.data.local.dao.WaterActivityDao
import com.saveourwater.data.local.entities.EcoGoal
import com.saveourwater.data.local.entities.WaterActivity

/**
 * Room Database for Save Our Water app
 * PHASE1-DB-P0-006: Build AppDatabase Class
 */
@Database(
    entities = [
        WaterActivity::class,
        EcoGoal::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun waterActivityDao(): WaterActivityDao
    abstract fun ecoGoalDao(): EcoGoalDao

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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

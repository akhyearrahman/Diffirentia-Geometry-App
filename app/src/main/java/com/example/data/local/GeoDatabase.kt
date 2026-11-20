package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ModuleProgressEntity::class,
        StudySessionEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GeoDatabase : RoomDatabase() {
    abstract fun geoDao(): GeoDao

    companion object {
        @Volatile
        private var INSTANCE: GeoDatabase? = null

        fun getInstance(context: Context): GeoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GeoDatabase::class.java,
                    "geomanifold_offline.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

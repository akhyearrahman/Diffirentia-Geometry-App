package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GeoDao {
    @Query("SELECT * FROM module_progress")
    fun observeAllModuleProgress(): Flow<List<ModuleProgressEntity>>

    @Query("SELECT * FROM module_progress WHERE moduleId = :moduleId LIMIT 1")
    fun observeModuleProgress(moduleId: String): Flow<ModuleProgressEntity?>

    @Query("SELECT * FROM module_progress WHERE moduleId = :moduleId LIMIT 1")
    suspend fun getModuleProgressOnce(moduleId: String): ModuleProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModuleProgress(entity: ModuleProgressEntity)

    @Query("SELECT * FROM study_sessions ORDER BY completedAt DESC LIMIT 30")
    fun observeRecentSessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity)

    @Query("SELECT * FROM user_settings WHERE id = 0 LIMIT 1")
    fun observeUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 0 LIMIT 1")
    suspend fun getUserSettingsOnce(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserSettings(settings: UserSettingsEntity)
}

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "module_progress")
data class ModuleProgressEntity(
    @PrimaryKey val moduleId: String,
    val isCompleted: Boolean = false,
    val sectionsReadMask: Int = 0, // bitmask for sections 0..3
    val q1SelectedOption: Int = -1,
    val q1Note: String = "",
    val q1Verified: Boolean = false,
    val q2SelectedOption: Int = -1,
    val q2Note: String = "",
    val q2Verified: Boolean = false,
    val lastStudiedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleId: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 0,
    val darkModeEnabled: Boolean = true,
    val learnerName: String = "গণিত অভিযাত্রী",
    val dailyGoalMinutes: Int = 35,
    val lastOpenedModuleId: String = "1.1"
)

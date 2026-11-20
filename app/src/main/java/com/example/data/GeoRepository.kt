package com.example.data

import com.example.data.local.GeoDao
import com.example.data.local.ModuleProgressEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GeoRepository(private val dao: GeoDao) {
    val allProgress: Flow<Map<String, ModuleProgressEntity>> =
        dao.observeAllModuleProgress().map { list -> list.associateBy { it.moduleId } }

    val module11Progress: Flow<ModuleProgressEntity> =
        dao.observeModuleProgress("1.1").map { it ?: ModuleProgressEntity(moduleId = "1.1") }

    val recentSessions: Flow<List<StudySessionEntity>> = dao.observeRecentSessions()

    val userSettings: Flow<UserSettingsEntity> =
        dao.observeUserSettings().map { it ?: UserSettingsEntity() }

    suspend fun toggleSectionRead(moduleId: String, sectionBit: Int) {
        val current = dao.getModuleProgressOnce(moduleId) ?: ModuleProgressEntity(moduleId = moduleId)
        val updatedMask = current.sectionsReadMask or (1 shl sectionBit)
        dao.upsertModuleProgress(
            current.copy(
                sectionsReadMask = updatedMask,
                lastStudiedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveIntuitionAnswer(
        moduleId: String,
        questionNumber: Int,
        selectedOption: Int,
        note: String,
        verified: Boolean
    ) {
        val current = dao.getModuleProgressOnce(moduleId) ?: ModuleProgressEntity(moduleId = moduleId)
        val updated = if (questionNumber == 1) {
            current.copy(
                q1SelectedOption = selectedOption,
                q1Note = note,
                q1Verified = verified,
                lastStudiedAt = System.currentTimeMillis()
            )
        } else {
            current.copy(
                q2SelectedOption = selectedOption,
                q2Note = note,
                q2Verified = verified,
                lastStudiedAt = System.currentTimeMillis()
            )
        }
        dao.upsertModuleProgress(updated)
    }

    suspend fun setModuleCompleted(moduleId: String, completed: Boolean) {
        val current = dao.getModuleProgressOnce(moduleId) ?: ModuleProgressEntity(moduleId = moduleId)
        dao.upsertModuleProgress(
            current.copy(
                isCompleted = completed,
                sectionsReadMask = if (completed) 0b1111 else current.sectionsReadMask,
                lastStudiedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun logStudySession(moduleId: String, durationMinutes: Int, note: String) {
        dao.insertStudySession(
            StudySessionEntity(
                moduleId = moduleId,
                durationMinutes = durationMinutes,
                note = note
            )
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        val current = dao.getUserSettingsOnce() ?: UserSettingsEntity()
        dao.upsertUserSettings(current.copy(darkModeEnabled = enabled))
    }

    suspend fun updateLearnerProfile(name: String, dailyGoalMinutes: Int) {
        val current = dao.getUserSettingsOnce() ?: UserSettingsEntity()
        dao.upsertUserSettings(
            current.copy(
                learnerName = name.ifBlank { "গণিত অভিযাত্রী" },
                dailyGoalMinutes = dailyGoalMinutes.coerceIn(15, 90)
            )
        )
    }
}

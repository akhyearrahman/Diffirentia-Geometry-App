package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeoRepository
import com.example.data.StudyCurriculum
import com.example.data.local.GeoDatabase
import com.example.data.local.ModuleProgressEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.UserSettingsEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OverallProgressStats(
    val completedModules: Int = 0,
    val totalModules: Int = StudyCurriculum.allModules.size,
    val totalStudyMinutes: Int = 0,
    val totalSessions: Int = 0,
    val module11SectionsRead: Int = 0,
    val intuitionChecksVerified: Int = 0
) {
    val progressFraction: Float
        get() = if (totalModules > 0) completedModules.toFloat() / totalModules.toFloat() else 0f
}

data class StudyTimerState(
    val targetMinutes: Int = 30,
    val remainingSeconds: Int = 30 * 60,
    val isRunning: Boolean = false
)

class GeoManifoldViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GeoRepository(GeoDatabase.getInstance(application).geoDao())

    val allProgress: StateFlow<Map<String, ModuleProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val module11Progress: StateFlow<ModuleProgressEntity> = repository.module11Progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ModuleProgressEntity("1.1"))

    val recentSessions: StateFlow<List<StudySessionEntity>> = repository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettingsEntity())

    val overallStats: StateFlow<OverallProgressStats> = combine(
        allProgress,
        recentSessions
    ) { progressMap, sessions ->
        val m11 = progressMap["1.1"]
        val sectionsCount = if (m11 == null) 0 else Integer.bitCount(m11.sectionsReadMask and 0b1111)
        val verifiedCount = (if (m11?.q1Verified == true) 1 else 0) + (if (m11?.q2Verified == true) 1 else 0)
        OverallProgressStats(
            completedModules = progressMap.values.count { it.isCompleted },
            totalModules = StudyCurriculum.allModules.size,
            totalStudyMinutes = sessions.sumOf { it.durationMinutes },
            totalSessions = sessions.size,
            module11SectionsRead = sectionsCount,
            intuitionChecksVerified = verifiedCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverallProgressStats())

    // --- Interactive Vector Lab State ---
    private val _vecU = MutableStateFlow(Vec2(2.6f, 2.2f))
    val vecU: StateFlow<Vec2> = _vecU.asStateFlow()

    private val _vecV = MutableStateFlow(Vec2(3.2f, 0.8f))
    val vecV: StateFlow<Vec2> = _vecV.asStateFlow()

    private val _scalarFactor = MutableStateFlow(1.5f)
    val scalarFactor: StateFlow<Float> = _scalarFactor.asStateFlow()

    private val _selectedAnimOp = MutableStateFlow(VectorAnimOp.PROJECTION)
    val selectedAnimOp: StateFlow<VectorAnimOp> = _selectedAnimOp.asStateFlow()

    private val _metricTensor = MutableStateFlow(MetricTensor2D(g11 = 1f, g12 = 0f, g22 = 1f))
    val metricTensor: StateFlow<MetricTensor2D> = _metricTensor.asStateFlow()

    private val _manifoldState = MutableStateFlow(ManifoldState())
    val manifoldState: StateFlow<ManifoldState> = _manifoldState.asStateFlow()

    // --- Study Timer State (BCS Micro-Class 30/45 min) ---
    private val _timerState = MutableStateFlow(StudyTimerState())
    val timerState: StateFlow<StudyTimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    fun updateVecU(newU: Vec2) {
        _vecU.value = Vec2(newU.x.coerceIn(-4.5f, 4.5f), newU.y.coerceIn(-4.5f, 4.5f))
    }

    fun updateVecV(newV: Vec2) {
        _vecV.value = Vec2(newV.x.coerceIn(-4.5f, 4.5f), newV.y.coerceIn(-4.5f, 4.5f))
    }

    fun applyVectorPreset(u: Vec2, v: Vec2) {
        _vecU.value = u
        _vecV.value = v
    }

    fun doubleVectorV() {
        val current = _vecV.value
        val doubled = Vec2(current.x * 2f, current.y * 2f)
        if (doubled.norm() <= 5.2f) {
            _vecV.value = Vec2(doubled.x.coerceIn(-4.5f, 4.5f), doubled.y.coerceIn(-4.5f, 4.5f))
        } else {
            // Reset to clean 60-degree pair and double v
            _vecU.value = Vec2(1.5f, 2.6f)
            _vecV.value = Vec2(4.0f, 0.0f)
        }
    }

    fun updateScalarFactor(c: Float) {
        _scalarFactor.value = c.coerceIn(-2.0f, 2.5f)
    }

    fun selectAnimOp(op: VectorAnimOp) {
        _selectedAnimOp.value = op
    }

    fun updateMetricTensor(g11: Float, g12: Float, g22: Float) {
        val safeG11 = g11.coerceIn(0.35f, 5.0f)
        val safeG22 = g22.coerceIn(0.35f, 5.0f)
        val maxOffDiag = kotlin.math.sqrt(safeG11 * safeG22) * 0.78f
        val safeG12 = g12.coerceIn(-maxOffDiag, maxOffDiag)
        _metricTensor.value = MetricTensor2D(safeG11, safeG12, safeG22)
    }

    fun selectManifoldKind(kind: ManifoldKind) {
        _manifoldState.update { it.copy(kind = kind) }
    }

    fun updateManifoldCurvature(scale: Float) {
        _manifoldState.update { it.copy(curvatureScale = scale.coerceIn(0.2f, 1.3f)) }
    }

    fun rotateManifold(deltaYaw: Float, deltaPitch: Float) {
        _manifoldState.update {
            it.copy(
                yawDeg = (it.yawDeg + deltaYaw) % 360f,
                pitchDeg = (it.pitchDeg + deltaPitch).coerceIn(-65f, 65f)
            )
        }
    }

    fun updateManifoldPoint(u: Float, v: Float) {
        _manifoldState.update {
            it.copy(
                pointU = u.coerceIn(-0.85f, 0.85f),
                pointV = v.coerceIn(-0.85f, 0.85f)
            )
        }
    }

    // --- Persistence Actions ---
    fun markSectionRead(moduleId: String, sectionIndex: Int) {
        viewModelScope.launch {
            repository.toggleSectionRead(moduleId, sectionIndex)
        }
    }

    fun submitIntuitionAnswer(
        moduleId: String,
        questionNumber: Int,
        selectedOption: Int,
        note: String,
        isCorrect: Boolean
    ) {
        viewModelScope.launch {
            repository.saveIntuitionAnswer(
                moduleId = moduleId,
                questionNumber = questionNumber,
                selectedOption = selectedOption,
                note = note,
                verified = isCorrect
            )
        }
    }

    fun setModuleCompleted(moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.setModuleCompleted(moduleId, completed)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }

    fun updateLearnerProfile(name: String, dailyGoalMinutes: Int) {
        viewModelScope.launch {
            repository.updateLearnerProfile(name, dailyGoalMinutes)
        }
    }

    // --- Micro-Class Timer ---
    fun setTimerDuration(minutes: Int) {
        timerJob?.cancel()
        _timerState.value = StudyTimerState(
            targetMinutes = minutes,
            remainingSeconds = minutes * 60,
            isRunning = false
        )
    }

    fun toggleTimer() {
        val current = _timerState.value
        if (current.isRunning) {
            timerJob?.cancel()
            _timerState.update { it.copy(isRunning = false) }
        } else {
            _timerState.update { it.copy(isRunning = true) }
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                    delay(1000L)
                    _timerState.update { st ->
                        val next = (st.remainingSeconds - 1).coerceAtLeast(0)
                        st.copy(remainingSeconds = next)
                    }
                }
                if (_timerState.value.remainingSeconds == 0 && _timerState.value.isRunning) {
                    val mins = _timerState.value.targetMinutes
                    _timerState.update { it.copy(isRunning = false, remainingSeconds = mins * 60) }
                    repository.logStudySession("1.1", mins, "মাইক্রো-ক্লাস সেশন সম্পন্ন")
                }
            }
        }
    }

    fun logQuickSession(minutes: Int, note: String) {
        viewModelScope.launch {
            repository.logStudySession("1.1", minutes, note)
        }
    }
}

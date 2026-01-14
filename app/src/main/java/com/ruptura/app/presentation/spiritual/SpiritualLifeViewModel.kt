package com.ruptura.app.presentation.spiritual

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruptura.app.domain.model.CompletionType
import com.ruptura.app.domain.usecase.spiritual.GetTodaySpiritualActivitiesUseCase
import com.ruptura.app.domain.usecase.spiritual.MarkSpiritualActivityCompleteUseCase
import com.ruptura.app.domain.usecase.spiritual.StartSpiritualActivitySessionUseCase
import com.ruptura.app.service.FocusLockService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SpiritualLifeViewModel @Inject constructor(
    private val getTodayActivitiesUseCase: GetTodaySpiritualActivitiesUseCase,
    private val markCompleteUseCase: MarkSpiritualActivityCompleteUseCase,
    private val startSessionUseCase: StartSpiritualActivitySessionUseCase,
    private val getAllSchedulesUseCase: com.ruptura.app.domain.usecase.schedule.GetAllSchedulesUseCase,
    private val addScheduleUseCase: com.ruptura.app.domain.usecase.schedule.AddScheduleUseCase,
    private val updateScheduleUseCase: com.ruptura.app.domain.usecase.schedule.UpdateScheduleUseCase,
    private val deleteScheduleUseCase: com.ruptura.app.domain.usecase.schedule.DeleteScheduleUseCase,
    private val toggleScheduleUseCase: com.ruptura.app.domain.usecase.schedule.ToggleScheduleUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpiritualLifeUiState())
    val uiState: StateFlow<SpiritualLifeUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
        loadSchedules()
        scheduleNextMidnightReset()
    }

    fun loadActivities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTodayActivitiesUseCase()
                .onSuccess { activities ->
                    _uiState.update { it.copy(activities = activities, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao carregar atividades: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun loadSchedules() {
        viewModelScope.launch {
            getAllSchedulesUseCase()
                .collect { schedulesWithActivity ->
                    // Group schedules by activityId
                    val schedulesByActivity = schedulesWithActivity
                        .groupBy { it.schedule.activityId }
                        .mapValues { (_, schedules) -> schedules.map { it.schedule } }

                    _uiState.update { it.copy(schedulesByActivity = schedulesByActivity) }
                }
        }
    }

    private fun loadActivitiesInBackground() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            getTodayActivitiesUseCase()
                .onSuccess { activities ->
                    _uiState.update { it.copy(activities = activities, isRefreshing = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = "Erro ao carregar atividades: ${error.message}"
                        )
                    }
                }
        }
    }

    fun markComplete(activityId: String) {
        viewModelScope.launch {
            markCompleteUseCase(activityId, CompletionType.MANUAL_CHECK)
                .onSuccess { loadActivitiesInBackground() }
                .onFailure { error ->
                    _uiState.update { it.copy(error = "Erro ao marcar completo: ${error.message}") }
                }
        }
    }

    fun startActivitySession(activityId: String, customDurationSeconds: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(startingActivityId = activityId, error = null) }

            startSessionUseCase(activityId, customDurationSeconds)
                .onSuccess { session ->
                    val config = session.config
                    val intent = Intent(context, FocusLockService::class.java).apply {
                        action = FocusLockService.ACTION_START_SESSION
                        putExtra(FocusLockService.EXTRA_SESSION_CONFIG, config)
                    }

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }

                        _uiState.update {
                            it.copy(
                                startingActivityId = null,
                                sessionStarted = true
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                startingActivityId = null,
                                error = "Erro ao iniciar sessão: ${e.message}"
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            startingActivityId = null,
                            error = error.message ?: "Erro desconhecido"
                        )
                    }
                }
        }
    }

    fun showTimeSelection(activityId: String) {
        val activity = _uiState.value.activities
            .find { it.activity.id == activityId }
            ?.activity

        if (activity?.supportsTimeSelection() == true) {
            _uiState.update {
                it.copy(
                    showingTimeSelectionForActivity = activityId,
                    selectedDurationSeconds = activity.durationSeconds
                )
            }
        } else {
            startActivitySession(activityId, null)
        }
    }

    fun setSelectedDuration(durationSeconds: Int) {
        _uiState.update {
            it.copy(selectedDurationSeconds = durationSeconds)
        }
    }

    fun cancelTimeSelection() {
        _uiState.update {
            it.copy(
                showingTimeSelectionForActivity = null,
                selectedDurationSeconds = null
            )
        }
    }

    fun confirmAndStartSession() {
        val activityId = _uiState.value.showingTimeSelectionForActivity
        val durationSeconds = _uiState.value.selectedDurationSeconds

        if (activityId != null && durationSeconds != null) {
            startActivitySession(activityId, durationSeconds)
            cancelTimeSelection()
        }
    }

    fun clearSessionStartedFlag() {
        _uiState.update { it.copy(sessionStarted = false) }
    }

    private fun scheduleNextMidnightReset() {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                val delayMillis = ChronoUnit.MILLIS.between(now, nextMidnight)

                delay(delayMillis)
                loadActivities()
            }
        }
    }

    // Schedule Management Functions
    fun showAddScheduleDialog(activityId: String) {
        _uiState.update {
            it.copy(
                showingScheduleDialogForActivity = activityId,
                editingSchedule = null
            )
        }
    }

    fun showEditScheduleDialog(schedule: com.ruptura.app.domain.model.SpiritualSchedule) {
        _uiState.update {
            it.copy(
                showingScheduleDialogForActivity = schedule.activityId,
                editingSchedule = schedule
            )
        }
    }

    fun dismissScheduleDialog() {
        _uiState.update {
            it.copy(
                showingScheduleDialogForActivity = null,
                editingSchedule = null
            )
        }
    }

    fun saveSchedule(schedule: com.ruptura.app.domain.model.SpiritualSchedule) {
        viewModelScope.launch {
            try {
                if (schedule.id == 0L) {
                    addScheduleUseCase(schedule)
                } else {
                    updateScheduleUseCase(schedule)
                }
                dismissScheduleDialog()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao salvar horário: ${e.message}")
                }
            }
        }
    }

    fun deleteSchedule(schedule: com.ruptura.app.domain.model.SpiritualSchedule) {
        viewModelScope.launch {
            try {
                deleteScheduleUseCase(schedule)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao deletar horário: ${e.message}")
                }
            }
        }
    }

    fun toggleSchedule(scheduleId: Long, enabled: Boolean) {
        viewModelScope.launch {
            try {
                toggleScheduleUseCase(scheduleId, enabled)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao alterar horário: ${e.message}")
                }
            }
        }
    }
}

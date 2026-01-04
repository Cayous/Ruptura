package com.zenapp.focus.presentation.spiritual

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenapp.focus.domain.model.CompletionType
import com.zenapp.focus.domain.usecase.spiritual.GetTodaySpiritualActivitiesUseCase
import com.zenapp.focus.domain.usecase.spiritual.MarkSpiritualActivityCompleteUseCase
import com.zenapp.focus.domain.usecase.spiritual.StartSpiritualActivitySessionUseCase
import com.zenapp.focus.service.FocusLockService
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpiritualLifeUiState())
    val uiState: StateFlow<SpiritualLifeUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
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

    fun markComplete(activityId: String) {
        viewModelScope.launch {
            markCompleteUseCase(activityId, CompletionType.MANUAL_CHECK)
                .onSuccess { loadActivities() }
                .onFailure { error ->
                    _uiState.update { it.copy(error = "Erro ao marcar completo: ${error.message}") }
                }
        }
    }

    fun startActivitySession(activityId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(startingActivityId = activityId, error = null) }

            startSessionUseCase(activityId)
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
}

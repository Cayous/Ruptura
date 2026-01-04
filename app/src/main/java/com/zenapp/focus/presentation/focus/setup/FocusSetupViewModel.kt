package com.zenapp.focus.presentation.focus.setup

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenapp.focus.domain.model.SessionConfig
import com.zenapp.focus.domain.usecase.session.StartFocusSessionUseCase
import com.zenapp.focus.service.FocusLockService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusSetupUiState(
    val focusDurationMinutes: Int = 25,  // Padrão Pomodoro
    val breakDurationMinutes: Int = 5,
    val totalCycles: Int = 1,
    val monkModeEnabled: Boolean = false,
    val isStarting: Boolean = false,
    val sessionStarted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FocusSetupViewModel @Inject constructor(
    private val startSessionUseCase: StartFocusSessionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusSetupUiState())
    val uiState: StateFlow<FocusSetupUiState> = _uiState.asStateFlow()

    fun setFocusDuration(minutes: Int) {
        _uiState.update { it.copy(focusDurationMinutes = minutes) }
    }

    fun setBreakDuration(minutes: Int) {
        _uiState.update { it.copy(breakDurationMinutes = minutes) }
    }

    fun setCycles(cycles: Int) {
        _uiState.update { it.copy(totalCycles = cycles) }
    }

    fun setMonkMode(enabled: Boolean) {
        _uiState.update { it.copy(monkModeEnabled = enabled) }
    }

    fun startSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true, error = null) }

            val config = SessionConfig(
                focusDurationMinutes = _uiState.value.focusDurationMinutes,
                breakDurationMinutes = _uiState.value.breakDurationMinutes,
                totalCycles = _uiState.value.totalCycles,
                monkModeEnabled = _uiState.value.monkModeEnabled
            )

            // Start the service
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
                        isStarting = false,
                        sessionStarted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isStarting = false,
                        error = "Erro ao iniciar sessão: ${e.message}"
                    )
                }
            }
        }
    }
}

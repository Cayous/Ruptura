package com.zenapp.focus.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange
import com.zenapp.focus.domain.repository.UsageRepository
import com.zenapp.focus.domain.usecase.GetPeakHoursUseCase
import com.zenapp.focus.domain.usecase.GetTopUsedAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val topApps: List<AppUsageInfo> = emptyList(),
    val peakHours: List<HourlyUsage> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.TODAY,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopUsedAppsUseCase: GetTopUsedAppsUseCase,
    private val getPeakHoursUseCase: GetPeakHoursUseCase,
    private val repository: UsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUsageData()
    }

    fun loadUsageData(timeRange: TimeRange = TimeRange.TODAY) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val topApps = getTopUsedAppsUseCase(timeRange, limit = 10)
                val peakHours = getPeakHoursUseCase(timeRange)

                _uiState.update {
                    it.copy(
                        topApps = topApps,
                        peakHours = peakHours,
                        selectedTimeRange = timeRange,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao carregar dados: ${e.message}"
                    )
                }
            }
        }
    }

    fun onTimeRangeSelected(timeRange: TimeRange) {
        if (timeRange != _uiState.value.selectedTimeRange) {
            loadUsageData(timeRange)
        }
    }

    fun refresh() {
        repository.invalidateCache(_uiState.value.selectedTimeRange)
        loadUsageData(_uiState.value.selectedTimeRange)
    }
}

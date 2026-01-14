package com.ruptura.app.presentation.spiritual

import com.ruptura.app.domain.model.SpiritualActivityWithStatus
import com.ruptura.app.domain.model.SpiritualSchedule

data class SpiritualLifeUiState(
    val activities: List<SpiritualActivityWithStatus> = emptyList(),
    val schedulesByActivity: Map<String, List<SpiritualSchedule>> = emptyMap(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val startingActivityId: String? = null,
    val sessionStarted: Boolean = false,
    val showingTimeSelectionForActivity: String? = null,
    val selectedDurationSeconds: Int? = null,
    val showingScheduleDialogForActivity: String? = null,
    val editingSchedule: SpiritualSchedule? = null
)

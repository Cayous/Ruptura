package com.zenapp.focus.presentation.spiritual

import com.zenapp.focus.domain.model.SpiritualActivityWithStatus

data class SpiritualLifeUiState(
    val activities: List<SpiritualActivityWithStatus> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val startingActivityId: String? = null,
    val sessionStarted: Boolean = false
)

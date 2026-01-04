package com.zenapp.focus.domain.model

data class SpiritualActivityWithStatus(
    val activity: SpiritualActivity,
    val completion: SpiritualCompletion?,
    val isCompleted: Boolean = completion != null
)

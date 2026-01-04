package com.zenapp.focus.domain.model

data class SpiritualCompletion(
    val activityId: String,
    val completionDate: String,
    val completedAt: Long,
    val completionType: CompletionType,
    val sessionId: Long?
)

enum class CompletionType {
    TIMER,
    MANUAL_CHECK
}

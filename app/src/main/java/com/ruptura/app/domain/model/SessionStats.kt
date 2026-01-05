package com.ruptura.app.domain.model

data class SessionStats(
    val sessionId: Long,
    val totalFocusTimeMillis: Long,
    val completedCycles: Int,
    val plannedCycles: Int,
    val breakCount: Int,              // How many times emergency exit was used
    val sessionDurationMillis: Long,  // Total elapsed time
    val createdAt: Long,
    val completedAt: Long?
)

data class DailySessionStats(
    val date: String,                  // "2026-01-03"
    val totalFocusTimeMillis: Long,
    val totalBreakTimeMillis: Long,
    val sessionsCompleted: Int,
    val totalCycles: Int,
    val averageBreakCount: Float
)

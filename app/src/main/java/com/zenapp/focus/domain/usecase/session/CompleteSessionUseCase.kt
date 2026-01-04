package com.zenapp.focus.domain.usecase.session

import com.zenapp.focus.domain.model.FocusSession
import com.zenapp.focus.domain.model.SessionStats
import com.zenapp.focus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class CompleteSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(session: FocusSession): SessionStats {
        val stats = SessionStats(
            sessionId = session.id,
            totalFocusTimeMillis = session.totalFocusTimeMillis,
            completedCycles = session.currentCycle,
            plannedCycles = session.config.totalCycles,
            breakCount = session.breakCount,
            sessionDurationMillis = System.currentTimeMillis() - session.createdAt,
            createdAt = session.createdAt,
            completedAt = System.currentTimeMillis()
        )

        repository.completeSession(session.id, stats)
        return stats
    }
}

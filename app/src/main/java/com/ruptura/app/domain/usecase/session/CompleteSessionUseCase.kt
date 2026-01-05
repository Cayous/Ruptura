package com.ruptura.app.domain.usecase.session

import com.ruptura.app.domain.model.CompletionType
import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionStats
import com.ruptura.app.domain.repository.FocusSessionRepository
import com.ruptura.app.domain.repository.SpiritualRepository
import java.time.LocalDate
import javax.inject.Inject

class CompleteSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository,
    private val spiritualRepository: SpiritualRepository
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

        // Auto-complete spiritual activity if this session is linked to one
        val activityId = session.spiritualActivityId
        if (activityId != null) {
            try {
                val today = LocalDate.now().toString()
                val isCompleted = spiritualRepository.isActivityCompleted(activityId, today)

                // Check if already completed (race condition safety)
                if (!isCompleted) {
                    spiritualRepository.markActivityComplete(
                        activityId = activityId,
                        date = today,
                        completionType = CompletionType.TIMER,
                        sessionId = session.id
                    )
                }
            } catch (e: Exception) {
                // Log but don't fail session completion if spiritual activity completion fails
                android.util.Log.e(
                    "CompleteSessionUseCase",
                    "Failed to auto-complete spiritual activity $activityId",
                    e
                )
            }
        }

        return stats
    }
}

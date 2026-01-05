package com.ruptura.app.domain.usecase.session

import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionPhase
import com.ruptura.app.domain.model.SessionState
import com.ruptura.app.domain.repository.FocusSessionRepository
import javax.inject.Inject

class UpdateSessionPhaseUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(
        session: FocusSession,
        newPhase: SessionPhase,
        newState: SessionState? = null
    ): FocusSession {
        val now = System.currentTimeMillis()
        val duration = when (newPhase) {
            SessionPhase.FOCUS -> session.config.focusDurationMinutes * 60 * 1000L
            SessionPhase.BREAK -> session.config.breakDurationMinutes * 60 * 1000L
            SessionPhase.INTER_CYCLE_PAUSE -> 10 * 1000L  // 10 seconds
        }

        val updated = session.copy(
            currentPhase = newPhase,
            state = newState ?: session.state,
            phaseStartTime = now,
            phaseEndTime = now + duration
        )

        repository.updateSession(updated)
        return updated
    }
}

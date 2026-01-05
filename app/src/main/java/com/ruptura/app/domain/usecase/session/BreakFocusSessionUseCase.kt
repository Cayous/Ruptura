package com.ruptura.app.domain.usecase.session

import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionState
import com.ruptura.app.domain.repository.FocusSessionRepository
import javax.inject.Inject

class BreakFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(session: FocusSession): FocusSession {
        val updated = session.copy(
            breakCount = session.breakCount + 1,
            state = SessionState.CANCELLED
        )

        repository.updateSession(updated)
        return updated
    }
}

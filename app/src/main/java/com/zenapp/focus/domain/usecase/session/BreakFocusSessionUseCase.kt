package com.zenapp.focus.domain.usecase.session

import com.zenapp.focus.domain.model.FocusSession
import com.zenapp.focus.domain.model.SessionState
import com.zenapp.focus.domain.repository.FocusSessionRepository
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

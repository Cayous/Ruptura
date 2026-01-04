package com.zenapp.focus.domain.usecase.session

import com.zenapp.focus.domain.model.FocusSession
import com.zenapp.focus.domain.model.SessionConfig
import com.zenapp.focus.domain.repository.FocusSessionRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(config: SessionConfig): Result<FocusSession> {
        return try {
            // Check if there's already an active session
            val activeSession = repository.getActiveSession()
            if (activeSession != null) {
                return Result.failure(IllegalStateException("Session already active"))
            }

            val session = repository.createSession(config)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.zenapp.focus.domain.usecase.spiritual

import com.zenapp.focus.domain.model.FocusSession
import com.zenapp.focus.domain.model.SessionConfig
import com.zenapp.focus.domain.repository.FocusSessionRepository
import com.zenapp.focus.domain.repository.SpiritualRepository
import java.time.LocalDate
import javax.inject.Inject

class StartSpiritualActivitySessionUseCase @Inject constructor(
    private val spiritualRepository: SpiritualRepository,
    private val sessionRepository: FocusSessionRepository
) {
    suspend operator fun invoke(activityId: String): Result<FocusSession> {
        return try {
            val today = LocalDate.now().toString()
            if (spiritualRepository.isActivityCompleted(activityId, today)) {
                return Result.failure(IllegalStateException("Activity already completed today"))
            }

            val activities = spiritualRepository.getAllActivities()
            val activity = activities.find { it.id == activityId }
                ?: return Result.failure(IllegalArgumentException("Activity not found"))

            // Removed active session check - let the service/repository handle conflicts
            // This prevents race conditions during session transitions

            val config = SessionConfig(
                focusDurationMinutes = activity.getDurationMinutes(),
                breakDurationMinutes = 0,
                totalCycles = 1,
                monkModeEnabled = false,
                spiritualActivityId = activityId
            )

            val session = sessionRepository.createSession(config)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

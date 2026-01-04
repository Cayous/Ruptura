package com.zenapp.focus.domain.usecase.spiritual

import com.zenapp.focus.domain.model.CompletionType
import com.zenapp.focus.domain.repository.SpiritualRepository
import java.time.LocalDate
import javax.inject.Inject

class MarkSpiritualActivityCompleteUseCase @Inject constructor(
    private val repository: SpiritualRepository
) {
    suspend operator fun invoke(
        activityId: String,
        completionType: CompletionType = CompletionType.MANUAL_CHECK,
        sessionId: Long? = null
    ): Result<Unit> {
        return try {
            val today = LocalDate.now().toString()
            repository.markActivityComplete(activityId, today, completionType, sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

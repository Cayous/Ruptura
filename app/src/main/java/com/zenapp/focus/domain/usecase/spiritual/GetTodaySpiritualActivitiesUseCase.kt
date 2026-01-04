package com.zenapp.focus.domain.usecase.spiritual

import com.zenapp.focus.domain.model.SpiritualActivityWithStatus
import com.zenapp.focus.domain.repository.SpiritualRepository
import javax.inject.Inject

class GetTodaySpiritualActivitiesUseCase @Inject constructor(
    private val repository: SpiritualRepository
) {
    suspend operator fun invoke(): Result<List<SpiritualActivityWithStatus>> {
        return try {
            val activities = repository.getTodayActivitiesWithStatus()
            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

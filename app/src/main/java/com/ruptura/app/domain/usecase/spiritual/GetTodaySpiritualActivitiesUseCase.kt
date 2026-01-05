package com.ruptura.app.domain.usecase.spiritual

import com.ruptura.app.domain.model.SpiritualActivityWithStatus
import com.ruptura.app.domain.repository.SpiritualRepository
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

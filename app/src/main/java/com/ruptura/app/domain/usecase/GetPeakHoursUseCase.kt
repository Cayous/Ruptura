package com.ruptura.app.domain.usecase

import com.ruptura.app.domain.model.HourlyUsage
import com.ruptura.app.domain.model.TimeRange
import com.ruptura.app.domain.repository.UsageRepository
import javax.inject.Inject

class GetPeakHoursUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(timeRange: TimeRange): List<HourlyUsage> {
        return repository.getHourlyUsage(timeRange)
    }
}

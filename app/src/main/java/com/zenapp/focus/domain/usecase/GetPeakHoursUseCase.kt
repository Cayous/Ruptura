package com.zenapp.focus.domain.usecase

import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange
import com.zenapp.focus.domain.repository.UsageRepository
import javax.inject.Inject

class GetPeakHoursUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(timeRange: TimeRange): List<HourlyUsage> {
        return repository.getHourlyUsage(timeRange)
    }
}

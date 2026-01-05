package com.ruptura.app.domain.usecase

import com.ruptura.app.domain.model.AppUsageInfo
import com.ruptura.app.domain.model.TimeRange
import com.ruptura.app.domain.repository.UsageRepository
import javax.inject.Inject

class GetTopUsedAppsUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(timeRange: TimeRange, limit: Int = 10): List<AppUsageInfo> {
        return repository.getTopUsedApps(timeRange, limit)
    }
}

package com.zenapp.focus.domain.usecase

import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.TimeRange
import com.zenapp.focus.domain.repository.UsageRepository
import javax.inject.Inject

class GetTopUsedAppsUseCase @Inject constructor(
    private val repository: UsageRepository
) {
    suspend operator fun invoke(timeRange: TimeRange, limit: Int = 10): List<AppUsageInfo> {
        return repository.getTopUsedApps(timeRange, limit)
    }
}

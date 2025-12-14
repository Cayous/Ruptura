package com.zenapp.focus.domain.repository

import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange

interface UsageRepository {
    suspend fun getTopUsedApps(timeRange: TimeRange, limit: Int = 10): List<AppUsageInfo>

    suspend fun getHourlyUsage(timeRange: TimeRange): List<HourlyUsage>

    suspend fun hasUsagePermission(): Boolean
}

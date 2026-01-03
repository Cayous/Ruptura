package com.zenapp.focus.domain.repository

import com.zenapp.focus.domain.model.AppDetailData
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange

interface UsageRepository {
    suspend fun getTopUsedApps(timeRange: TimeRange, limit: Int = 10): List<AppUsageInfo>

    suspend fun getHourlyUsage(timeRange: TimeRange): List<HourlyUsage>

    suspend fun getAppDetailData(packageName: String, timeRange: TimeRange): AppDetailData?

    suspend fun hasUsagePermission(): Boolean

    fun invalidateCache(timeRange: TimeRange? = null)
}

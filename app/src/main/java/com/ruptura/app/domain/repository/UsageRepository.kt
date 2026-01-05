package com.ruptura.app.domain.repository

import com.ruptura.app.domain.model.AppDetailData
import com.ruptura.app.domain.model.AppUsageInfo
import com.ruptura.app.domain.model.HourlyUsage
import com.ruptura.app.domain.model.TimeRange

interface UsageRepository {
    suspend fun getTopUsedApps(timeRange: TimeRange, limit: Int = 10): List<AppUsageInfo>

    suspend fun getHourlyUsage(timeRange: TimeRange): List<HourlyUsage>

    suspend fun getAppDetailData(packageName: String, timeRange: TimeRange): AppDetailData?

    suspend fun hasUsagePermission(): Boolean

    fun invalidateCache(timeRange: TimeRange? = null)
}

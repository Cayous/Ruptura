package com.ruptura.app.data.cache

import android.app.usage.UsageStats
import com.ruptura.app.data.source.UsageStatsDataSource
import com.ruptura.app.domain.model.TimeRange

data class UsageDataCacheEntry(
    val stats: Map<String, UsageStats>,
    val events: List<UsageStatsDataSource.UsageEvent>,
    val timestamp: Long = System.currentTimeMillis()
)

data class LauncherCacheEntry(
    val isLaunchable: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object CacheTTL {
    const val TODAY_MILLIS = 15 * 60 * 1000L
    const val LAST_7_DAYS_MILLIS = 30 * 60 * 1000L
    const val LAST_30_DAYS_MILLIS = 60 * 60 * 1000L
    const val LAUNCHER_MILLIS = 24 * 60 * 60 * 1000L

    fun getTTL(timeRange: TimeRange): Long = when(timeRange) {
        TimeRange.TODAY -> TODAY_MILLIS
        TimeRange.LAST_7_DAYS -> LAST_7_DAYS_MILLIS
        TimeRange.LAST_30_DAYS -> LAST_30_DAYS_MILLIS
    }
}

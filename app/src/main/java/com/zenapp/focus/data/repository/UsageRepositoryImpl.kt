package com.zenapp.focus.data.repository

import com.zenapp.focus.data.mapper.UsageStatsMapper
import com.zenapp.focus.data.source.UsageStatsDataSource
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange
import com.zenapp.focus.domain.repository.UsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val dataSource: UsageStatsDataSource,
    private val mapper: UsageStatsMapper
) : UsageRepository {

    override suspend fun getTopUsedApps(timeRange: TimeRange, limit: Int): List<AppUsageInfo> =
        withContext(Dispatchers.IO) {
            val apps = fetchTopApps(timeRange, limit)

            // Fallback: If TODAY returns < 3 apps, use LAST_7_DAYS
            if (timeRange == TimeRange.TODAY && apps.size < 3) {
                return@withContext fetchTopApps(TimeRange.LAST_7_DAYS, limit)
            }

            apps
        }

    /**
     * Internal method to fetch top apps for a specific time range.
     * Extracted to allow fallback logic without duplication.
     */
    private suspend fun fetchTopApps(timeRange: TimeRange, limit: Int): List<AppUsageInfo> {
        val (startTime, endTime) = getTimeRange(timeRange)

        val stats = dataSource.queryUsageStats(startTime, endTime)
        val allEvents = dataSource.queryUsageEvents(startTime, endTime)

        val appUsageList = stats.mapNotNull { (packageName, usageStats) ->
            val appEvents = allEvents.filter { it.packageName == packageName }
            mapper.toDomainModel(usageStats, appEvents)
        }

        return appUsageList
            .filter { it.totalTimeMillis > 0 }
            .sortedByDescending { it.totalTimeMillis }
            .take(limit)
            .mapIndexed { index, app ->
                app.copy(rank = index + 1)
            }
    }

    override suspend fun getHourlyUsage(timeRange: TimeRange): List<HourlyUsage> =
        withContext(Dispatchers.IO) {
            val (startTime, endTime) = getTimeRange(timeRange)

            // Query all usage data
            val stats = dataSource.queryUsageStats(startTime, endTime)
            val events = dataSource.queryUsageEvents(startTime, endTime)

            // Aggregate hourly usage across all apps
            mapper.aggregateAllAppsHourlyUsage(stats, events)
        }

    override suspend fun hasUsagePermission(): Boolean {
        return dataSource.hasUsagePermission()
    }

    private fun getTimeRange(timeRange: TimeRange): Pair<Long, Long> {
        return when (timeRange) {
            TimeRange.TODAY -> dataSource.getTodayMillis()
            TimeRange.LAST_7_DAYS -> dataSource.getTimeRangeMillis(7)
            TimeRange.LAST_30_DAYS -> dataSource.getTimeRangeMillis(30)
        }
    }
}

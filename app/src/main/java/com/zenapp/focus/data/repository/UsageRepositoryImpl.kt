package com.zenapp.focus.data.repository

import com.zenapp.focus.data.cache.CacheTTL
import com.zenapp.focus.data.cache.UsageDataCacheEntry
import com.zenapp.focus.data.mapper.UsageStatsMapper
import com.zenapp.focus.data.source.UsageStatsDataSource
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange
import com.zenapp.focus.domain.repository.UsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val dataSource: UsageStatsDataSource,
    private val mapper: UsageStatsMapper
) : UsageRepository {

    private val usageDataCache = ConcurrentHashMap<TimeRange, UsageDataCacheEntry>()

    override suspend fun getTopUsedApps(timeRange: TimeRange, limit: Int): List<AppUsageInfo> =
        withContext(Dispatchers.IO) {
            val data = getCachedUsageData(timeRange)
            val apps = processTopApps(data, limit)

            // Fallback: If TODAY returns < 3 apps, use LAST_7_DAYS
            if (timeRange == TimeRange.TODAY && apps.size < 3) {
                val fallbackData = getCachedUsageData(TimeRange.LAST_7_DAYS)
                return@withContext processTopApps(fallbackData, limit)
            }

            apps
        }

    override suspend fun getHourlyUsage(timeRange: TimeRange): List<HourlyUsage> =
        withContext(Dispatchers.IO) {
            val data = getCachedUsageData(timeRange)
            mapper.aggregateAllAppsHourlyUsage(data.stats, data.events)
        }

    override suspend fun getAppDetailData(packageName: String, timeRange: TimeRange): com.zenapp.focus.domain.model.AppDetailData? =
        withContext(Dispatchers.IO) {
            // TODO: Implement full AppDetailData when needed
            null
        }

    /**
     * Unified method to fetch usage data with caching.
     * Checks cache first, fetches from data source on miss.
     */
    private suspend fun getCachedUsageData(timeRange: TimeRange): UsageDataCacheEntry {
        // Check cache
        val cached = usageDataCache[timeRange]
        if (cached != null && isValidCache(cached, timeRange)) {
            return cached
        }

        // Cache miss - fetch fresh data
        val (startTime, endTime) = getTimeRange(timeRange)
        val stats = dataSource.queryUsageStats(startTime, endTime)
        val events = dataSource.queryUsageEvents(startTime, endTime)

        val newCache = UsageDataCacheEntry(
            stats = stats,
            events = events,
            timestamp = System.currentTimeMillis()
        )

        usageDataCache[timeRange] = newCache
        return newCache
    }

    /**
     * Validates cache entry based on TTL and special rules.
     */
    private fun isValidCache(cache: UsageDataCacheEntry, timeRange: TimeRange): Boolean {
        val age = System.currentTimeMillis() - cache.timestamp
        val ttl = CacheTTL.getTTL(timeRange)

        // TODAY special case - invalidate at midnight
        if (timeRange == TimeRange.TODAY) {
            return age < ttl && isSameDay(cache.timestamp)
        }

        return age < ttl
    }

    /**
     * Checks if the cached data is from the same day.
     * Used to invalidate TODAY cache at midnight.
     */
    private fun isSameDay(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.timeInMillis = timestamp
        val cachedDay = calendar.get(Calendar.DAY_OF_YEAR)
        val cachedYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = now
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        return cachedDay == currentDay && cachedYear == currentYear
    }

    /**
     * Processes cached data into top used apps list.
     */
    private fun processTopApps(data: UsageDataCacheEntry, limit: Int): List<AppUsageInfo> {
        val appUsageList = data.stats.mapNotNull { (packageName, usageStats) ->
            val appEvents = data.events.filter { it.packageName == packageName }
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

    /**
     * Invalidates cache for a specific time range or all caches.
     */
    override fun invalidateCache(timeRange: TimeRange?) {
        if (timeRange != null) {
            usageDataCache.remove(timeRange)
        } else {
            usageDataCache.clear()
        }
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

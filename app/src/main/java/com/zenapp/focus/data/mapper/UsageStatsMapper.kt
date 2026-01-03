package com.zenapp.focus.data.mapper

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.zenapp.focus.data.source.UsageStatsDataSource
import com.zenapp.focus.domain.model.AppDetailStats
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.DailyUsage
import com.zenapp.focus.domain.model.HourlyUsage
import com.zenapp.focus.domain.model.TimeRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class UsageStatsMapper @Inject constructor(
    private val packageManager: PackageManager
) {
    fun toDomainModel(
        stats: UsageStats,
        events: List<UsageStatsDataSource.UsageEvent>,
        rank: Int = 0
    ): AppUsageInfo? {
        return try {
            val appInfo = packageManager.getApplicationInfo(stats.packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val icon = try {
                packageManager.getApplicationIcon(stats.packageName)
            } catch (e: Exception) {
                null
            }

            val hourlyUsage = aggregateHourlyUsage(events)
            val launchCount = events.count { it.eventType == UsageEvents.Event.ACTIVITY_RESUMED }

            AppUsageInfo(
                packageName = stats.packageName,
                appName = appName,
                icon = icon,
                totalTimeMillis = stats.totalTimeInForeground,
                lastUsedTimestamp = stats.lastTimeUsed,
                hourlyUsage = hourlyUsage,
                launchCount = launchCount,
                rank = rank
            )
        } catch (e: Exception) {
            null // App might be uninstalled
        }
    }

    fun aggregateHourlyUsage(events: List<UsageStatsDataSource.UsageEvent>): List<HourlyUsage> {
        val hourMap = mutableMapOf<Int, Long>()
        val launchMap = mutableMapOf<Int, Int>()

        // Sort events by timestamp
        val sortedEvents = events.sortedBy { it.timestamp }

        var lastResumedEvent: UsageStatsDataSource.UsageEvent? = null

        sortedEvents.forEach { event ->
            val hour = Calendar.getInstance().apply {
                timeInMillis = event.timestamp
            }.get(Calendar.HOUR_OF_DAY)

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    lastResumedEvent = event
                    launchMap[hour] = launchMap.getOrDefault(hour, 0) + 1
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    lastResumedEvent?.let { resumed ->
                        val duration = event.timestamp - resumed.timestamp
                        if (duration > 0) {
                            hourMap[hour] = hourMap.getOrDefault(hour, 0) + duration
                        }
                        lastResumedEvent = null
                    }
                }
            }
        }

        // Create list for all 24 hours
        return (0..23).map { hour ->
            HourlyUsage(
                hour = hour,
                usageMillis = hourMap[hour] ?: 0,
                launchCount = launchMap[hour] ?: 0
            )
        }
    }

    fun aggregateAllAppsHourlyUsage(
        allStats: Map<String, UsageStats>,
        allEvents: List<UsageStatsDataSource.UsageEvent>
    ): List<HourlyUsage> {
        val hourMap = mutableMapOf<Int, Long>()

        // Group events by package
        val eventsByPackage = allEvents.groupBy { it.packageName }

        eventsByPackage.forEach { (_, events) ->
            val hourlyUsage = aggregateHourlyUsage(events)
            hourlyUsage.forEach { usage ->
                hourMap[usage.hour] = hourMap.getOrDefault(usage.hour, 0) + usage.usageMillis
            }
        }

        return (0..23).map { hour ->
            HourlyUsage(
                hour = hour,
                usageMillis = hourMap[hour] ?: 0
            )
        }
    }

    fun calculateDetailStats(
        events: List<UsageStatsDataSource.UsageEvent>,
        hourlyUsage: List<HourlyUsage>
    ): AppDetailStats {
        val avgSession = calculateAverageSession(events)
        val peakHour = hourlyUsage
            .sortedWith(
                compareByDescending<HourlyUsage> { it.usageMillis }
                    .thenBy { it.hour }
            )
            .firstOrNull()?.hour ?: 0

        val totalLaunches = events.count { it.eventType == UsageEvents.Event.ACTIVITY_RESUMED }
        val totalUsage = hourlyUsage.sumOf { it.usageMillis }

        return AppDetailStats(
            averageSessionMillis = avgSession,
            peakHour = peakHour,
            totalLaunches = totalLaunches,
            totalUsageMillis = totalUsage
        )
    }

    fun aggregateDailyUsage(
        events: List<UsageStatsDataSource.UsageEvent>,
        timeRange: TimeRange
    ): List<DailyUsage> {
        val dayMap = mutableMapOf<String, DailyUsageData>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        // Sort events by timestamp
        val sortedEvents = events.sortedBy { it.timestamp }
        var lastResumedEvent: UsageStatsDataSource.UsageEvent? = null

        sortedEvents.forEach { event ->
            calendar.timeInMillis = event.timestamp
            val year = calendar.get(Calendar.YEAR)
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val dayKey = "$year-$dayOfYear"

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    lastResumedEvent = event

                    // Initialize day if not exists
                    if (!dayMap.containsKey(dayKey)) {
                        dayMap[dayKey] = DailyUsageData(
                            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
                            dateString = dateFormat.format(calendar.time),
                            usageMillis = 0,
                            launchCount = 0
                        )
                    }

                    dayMap[dayKey]?.let {
                        it.launchCount++
                    }
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    lastResumedEvent?.let { resumed ->
                        val duration = event.timestamp - resumed.timestamp
                        if (duration > 0) {
                            // Add usage to the day
                            dayMap[dayKey]?.let {
                                it.usageMillis += duration
                            }
                        }
                        lastResumedEvent = null
                    }
                }
            }
        }

        // Convert to DailyUsage list and take last N days
        val daysToTake = when (timeRange) {
            TimeRange.TODAY -> 1
            TimeRange.LAST_7_DAYS -> 7
            TimeRange.LAST_30_DAYS -> 30
        }

        return dayMap.entries
            .map { (_, data) ->
                DailyUsage(
                    dayOfWeek = data.dayOfWeek,
                    dateString = data.dateString,
                    usageMillis = data.usageMillis,
                    launchCount = data.launchCount
                )
            }
            .sortedBy { it.dateString }
            .takeLast(daysToTake)
    }

    private fun calculateAverageSession(events: List<UsageStatsDataSource.UsageEvent>): Long {
        val sessions = mutableListOf<Long>()
        var lastResumed: Long? = null

        events.sortedBy { it.timestamp }.forEach { event ->
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    lastResumed = event.timestamp
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    lastResumed?.let { resumed ->
                        val duration = event.timestamp - resumed
                        // Filter outliers: sessions > 1 hour are likely incomplete
                        if (duration > 0 && duration < 3600000) {
                            sessions.add(duration)
                        }
                    }
                    lastResumed = null
                }
            }
        }

        return if (sessions.isNotEmpty()) {
            sessions.average().toLong()
        } else {
            0
        }
    }

    private data class DailyUsageData(
        val dayOfWeek: Int,
        val dateString: String,
        var usageMillis: Long,
        var launchCount: Int
    )
}

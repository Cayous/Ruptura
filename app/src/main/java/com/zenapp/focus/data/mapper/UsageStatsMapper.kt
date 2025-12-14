package com.zenapp.focus.data.mapper

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.zenapp.focus.data.source.UsageStatsDataSource
import com.zenapp.focus.domain.model.AppUsageInfo
import com.zenapp.focus.domain.model.HourlyUsage
import java.util.Calendar
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
}

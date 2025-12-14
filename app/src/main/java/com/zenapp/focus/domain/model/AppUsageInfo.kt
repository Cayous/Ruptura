package com.zenapp.focus.domain.model

import android.graphics.drawable.Drawable
import java.util.concurrent.TimeUnit

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val totalTimeMillis: Long,
    val lastUsedTimestamp: Long,
    val hourlyUsage: List<HourlyUsage>,
    val launchCount: Int = 0,
    val rank: Int = 0
) {
    val formattedTime: String
        get() = formatDuration(totalTimeMillis)

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}

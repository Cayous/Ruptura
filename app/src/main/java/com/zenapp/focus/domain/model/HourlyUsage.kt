package com.zenapp.focus.domain.model

data class HourlyUsage(
    val hour: Int,              // 0-23
    val usageMillis: Long,
    val launchCount: Int = 0
) {
    val formattedHour: String
        get() = "${hour}:00"

    val usageMinutes: Float
        get() = usageMillis / 60000f
}

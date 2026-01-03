package com.zenapp.focus.domain.model

data class AppDetailData(
    val appInfo: AppUsageInfo,
    val stats: AppDetailStats,
    val dailyUsage: List<DailyUsage>,
    val hourlyUsage: List<HourlyUsage>
)

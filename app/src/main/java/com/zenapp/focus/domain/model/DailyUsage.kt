package com.zenapp.focus.domain.model

data class DailyUsage(
    val dayOfWeek: Int,        // 1-7 (Calendar constants)
    val dateString: String,    // "Dec 13" para display
    val usageMillis: Long,     // Uso total do dia
    val launchCount: Int = 0   // Total de aberturas no dia
)

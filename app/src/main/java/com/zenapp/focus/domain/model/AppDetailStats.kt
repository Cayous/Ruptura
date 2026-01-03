package com.zenapp.focus.domain.model

data class AppDetailStats(
    val averageSessionMillis: Long,  // Média de duração por sessão
    val peakHour: Int,                // Hora com mais uso (0-23)
    val totalLaunches: Int,           // Total de aberturas
    val totalUsageMillis: Long        // Tempo total
) {
    val formattedAvgSession: String
        get() {
            val minutes = (averageSessionMillis / 60000).toInt()
            val hours = minutes / 60
            val remainingMinutes = minutes % 60

            return when {
                hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }
        }

    val formattedPeakHour: String
        get() {
            val hour = if (peakHour == 0) 12 else if (peakHour > 12) peakHour - 12 else peakHour
            val period = if (peakHour < 12) "AM" else "PM"
            return "$hour $period"
        }

    val formattedTotalUsage: String
        get() {
            val minutes = (totalUsageMillis / 60000).toInt()
            val hours = minutes / 60
            val remainingMinutes = minutes % 60

            return when {
                hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }
        }
}

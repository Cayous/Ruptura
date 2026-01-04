package com.zenapp.focus.domain.model

data class SpiritualActivity(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val orderIndex: Int
) {
    fun getFormattedDuration(): String {
        return when {
            durationSeconds < 60 -> "$durationSeconds segundos"
            durationSeconds < 3600 -> {
                val minutes = durationSeconds / 60
                "$minutes minuto${if (minutes > 1) "s" else ""}"
            }
            else -> {
                val hours = durationSeconds / 3600
                "$hours hora${if (hours > 1) "s" else ""}"
            }
        }
    }

    fun getDurationMinutes(): Int = (durationSeconds + 59) / 60
}

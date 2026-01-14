package com.ruptura.app.domain.model

enum class PeriodOfDay {
    MORNING,
    AFTERNOON,
    NIGHT;

    companion object {
        fun fromMinutesOfDay(minutes: Int): PeriodOfDay {
            return when (minutes) {
                in 0..719 -> MORNING // 00:00 - 11:59
                in 720..1079 -> AFTERNOON // 12:00 - 17:59
                else -> NIGHT // 18:00 - 23:59
            }
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            MORNING -> "Manhã"
            AFTERNOON -> "Tarde"
            NIGHT -> "Noite"
        }
    }

    fun getEmoji(): String {
        return when (this) {
            MORNING -> "🌅"
            AFTERNOON -> "☀️"
            NIGHT -> "🌙"
        }
    }
}

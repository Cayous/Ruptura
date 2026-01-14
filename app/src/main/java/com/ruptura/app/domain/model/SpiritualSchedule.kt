package com.ruptura.app.domain.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class SpiritualSchedule(
    val id: Long = 0,
    val activityId: String,
    val minutesOfDay: Int, // 0-1439
    val periodOfDay: PeriodOfDay,
    val isEnabled: Boolean = true,
    val requiresExactAlarm: Boolean = false,
    val enableSound: Boolean = true,
    val enableVibration: Boolean = true,
    val autoRemoveAfterMinutes: Int? = null,
    val completionScope: CompletionScope = CompletionScope.DAILY
) {
    fun getFormattedTime(): String {
        val hours = minutesOfDay / 60
        val minutes = minutesOfDay % 60
        val time = LocalTime.of(hours, minutes)
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getLocalTime(): LocalTime {
        val hours = minutesOfDay / 60
        val minutes = minutesOfDay % 60
        return LocalTime.of(hours, minutes)
    }

    companion object {
        fun minutesOfDayFromTime(hour: Int, minute: Int): Int {
            return hour * 60 + minute
        }

        fun minutesOfDayFromLocalTime(time: LocalTime): Int {
            return time.hour * 60 + time.minute
        }
    }
}

data class SpiritualScheduleWithActivity(
    val schedule: SpiritualSchedule,
    val activity: SpiritualActivity
)

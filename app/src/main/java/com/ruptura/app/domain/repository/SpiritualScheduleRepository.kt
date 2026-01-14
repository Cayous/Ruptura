package com.ruptura.app.domain.repository

import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.model.SpiritualScheduleWithActivity
import kotlinx.coroutines.flow.Flow

interface SpiritualScheduleRepository {
    fun getAllSchedules(): Flow<List<SpiritualSchedule>>
    fun getAllSchedulesWithActivity(): Flow<List<SpiritualScheduleWithActivity>>
    suspend fun getEnabledSchedules(): List<SpiritualSchedule>
    suspend fun getScheduleById(id: Long): SpiritualSchedule?
    suspend fun insertSchedule(schedule: SpiritualSchedule): Long
    suspend fun insertSchedules(schedules: List<SpiritualSchedule>)
    suspend fun updateSchedule(schedule: SpiritualSchedule)
    suspend fun deleteSchedule(schedule: SpiritualSchedule)
    suspend fun toggleEnabled(id: Long, enabled: Boolean)
    suspend fun deleteAll()

    // Snooze management
    suspend fun insertSnooze(scheduleId: Long, snoozeUntilTimestamp: Long, activityId: String)
    suspend fun deleteSnooze(scheduleId: Long)
    suspend fun getAllActiveSnoozes(currentTimestamp: Long): List<Pair<Long, Long>> // scheduleId, timestamp
    suspend fun deleteExpiredSnoozes(currentTimestamp: Long)
}

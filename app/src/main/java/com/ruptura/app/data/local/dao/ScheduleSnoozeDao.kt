package com.ruptura.app.data.local.dao

import androidx.room.*
import com.ruptura.app.data.local.entity.ScheduleSnoozeEntity

@Dao
interface ScheduleSnoozeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnooze(snooze: ScheduleSnoozeEntity)

    @Query("DELETE FROM schedule_snoozes WHERE scheduleId = :scheduleId")
    suspend fun deleteSnooze(scheduleId: Long)

    @Query("SELECT * FROM schedule_snoozes WHERE snoozeUntilTimestamp > :currentTimestamp")
    suspend fun getAllActiveSnoozes(currentTimestamp: Long): List<ScheduleSnoozeEntity>

    @Query("DELETE FROM schedule_snoozes WHERE snoozeUntilTimestamp <= :currentTimestamp")
    suspend fun deleteExpiredSnoozes(currentTimestamp: Long)

    @Query("SELECT * FROM schedule_snoozes")
    suspend fun getAllSnoozes(): List<ScheduleSnoozeEntity>
}

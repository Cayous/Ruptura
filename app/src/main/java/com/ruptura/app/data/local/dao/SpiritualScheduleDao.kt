package com.ruptura.app.data.local.dao

import androidx.room.*
import com.ruptura.app.data.local.entity.SpiritualScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpiritualScheduleDao {

    @Query("SELECT * FROM spiritual_schedules ORDER BY minutesOfDay ASC")
    fun getAllSchedules(): Flow<List<SpiritualScheduleEntity>>

    @Query("SELECT * FROM spiritual_schedules WHERE isEnabled = 1 ORDER BY minutesOfDay ASC")
    suspend fun getEnabledSchedules(): List<SpiritualScheduleEntity>

    @Query("SELECT * FROM spiritual_schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): SpiritualScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: SpiritualScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<SpiritualScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: SpiritualScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: SpiritualScheduleEntity)

    @Query("UPDATE spiritual_schedules SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleEnabled(id: Long, enabled: Boolean)

    @Query("DELETE FROM spiritual_schedules")
    suspend fun deleteAll()
}

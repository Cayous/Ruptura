package com.ruptura.app.data.repository

import com.ruptura.app.data.local.dao.ScheduleSnoozeDao
import com.ruptura.app.data.local.dao.SpiritualActivityDao
import com.ruptura.app.data.local.dao.SpiritualScheduleDao
import com.ruptura.app.data.mapper.ScheduleMapper
import com.ruptura.app.data.mapper.SpiritualMapper
import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.model.SpiritualScheduleWithActivity
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpiritualScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: SpiritualScheduleDao,
    private val snoozeDao: ScheduleSnoozeDao,
    private val activityDao: SpiritualActivityDao,
    private val scheduleMapper: ScheduleMapper,
    private val spiritualMapper: SpiritualMapper
) : SpiritualScheduleRepository {

    override fun getAllSchedules(): Flow<List<SpiritualSchedule>> {
        return scheduleDao.getAllSchedules().map { schedules ->
            schedules.map { scheduleMapper.toDomain(it) }
        }
    }

    override fun getAllSchedulesWithActivity(): Flow<List<SpiritualScheduleWithActivity>> {
        return scheduleDao.getAllSchedules().map { schedules ->
            withContext(Dispatchers.IO) {
                val activities = activityDao.getAllActivities().associateBy { it.id }
                schedules.mapNotNull { scheduleEntity ->
                    val activityEntity = activities[scheduleEntity.activityId]
                    if (activityEntity != null) {
                        SpiritualScheduleWithActivity(
                            schedule = scheduleMapper.toDomain(scheduleEntity),
                            activity = spiritualMapper.toDomain(activityEntity)
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    override suspend fun getEnabledSchedules(): List<SpiritualSchedule> =
        withContext(Dispatchers.IO) {
            scheduleDao.getEnabledSchedules().map { scheduleMapper.toDomain(it) }
        }

    override suspend fun getScheduleById(id: Long): SpiritualSchedule? =
        withContext(Dispatchers.IO) {
            scheduleDao.getScheduleById(id)?.let { scheduleMapper.toDomain(it) }
        }

    override suspend fun insertSchedule(schedule: SpiritualSchedule): Long =
        withContext(Dispatchers.IO) {
            scheduleDao.insertSchedule(scheduleMapper.toEntity(schedule))
        }

    override suspend fun insertSchedules(schedules: List<SpiritualSchedule>) =
        withContext(Dispatchers.IO) {
            scheduleDao.insertSchedules(schedules.map { scheduleMapper.toEntity(it) })
        }

    override suspend fun updateSchedule(schedule: SpiritualSchedule) =
        withContext(Dispatchers.IO) {
            scheduleDao.updateSchedule(scheduleMapper.toEntity(schedule))
        }

    override suspend fun deleteSchedule(schedule: SpiritualSchedule) =
        withContext(Dispatchers.IO) {
            scheduleDao.deleteSchedule(scheduleMapper.toEntity(schedule))
        }

    override suspend fun toggleEnabled(id: Long, enabled: Boolean) =
        withContext(Dispatchers.IO) {
            scheduleDao.toggleEnabled(id, enabled)
        }

    override suspend fun deleteAll() =
        withContext(Dispatchers.IO) {
            scheduleDao.deleteAll()
        }

    override suspend fun insertSnooze(scheduleId: Long, snoozeUntilTimestamp: Long, activityId: String) =
        withContext(Dispatchers.IO) {
            snoozeDao.insertSnooze(
                scheduleMapper.snoozeToEntity(scheduleId, snoozeUntilTimestamp, activityId)
            )
        }

    override suspend fun deleteSnooze(scheduleId: Long) =
        withContext(Dispatchers.IO) {
            snoozeDao.deleteSnooze(scheduleId)
        }

    override suspend fun getAllActiveSnoozes(currentTimestamp: Long): List<Pair<Long, Long>> =
        withContext(Dispatchers.IO) {
            snoozeDao.getAllActiveSnoozes(currentTimestamp).map {
                it.scheduleId to it.snoozeUntilTimestamp
            }
        }

    override suspend fun deleteExpiredSnoozes(currentTimestamp: Long) =
        withContext(Dispatchers.IO) {
            snoozeDao.deleteExpiredSnoozes(currentTimestamp)
        }
}

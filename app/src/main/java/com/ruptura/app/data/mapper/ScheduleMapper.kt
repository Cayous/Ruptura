package com.ruptura.app.data.mapper

import com.ruptura.app.data.local.entity.ScheduleSnoozeEntity
import com.ruptura.app.data.local.entity.SpiritualScheduleEntity
import com.ruptura.app.domain.model.CompletionScope
import com.ruptura.app.domain.model.PeriodOfDay
import com.ruptura.app.domain.model.SpiritualSchedule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleMapper @Inject constructor() {

    fun toDomain(entity: SpiritualScheduleEntity): SpiritualSchedule {
        return SpiritualSchedule(
            id = entity.id,
            activityId = entity.activityId,
            minutesOfDay = entity.minutesOfDay,
            periodOfDay = PeriodOfDay.valueOf(entity.periodOfDay),
            isEnabled = entity.isEnabled,
            requiresExactAlarm = entity.requiresExactAlarm,
            enableSound = entity.enableSound,
            enableVibration = entity.enableVibration,
            autoRemoveAfterMinutes = entity.autoRemoveAfterMinutes,
            completionScope = CompletionScope.valueOf(entity.completionScope)
        )
    }

    fun toEntity(domain: SpiritualSchedule): SpiritualScheduleEntity {
        return SpiritualScheduleEntity(
            id = domain.id,
            activityId = domain.activityId,
            minutesOfDay = domain.minutesOfDay,
            periodOfDay = domain.periodOfDay.name,
            isEnabled = domain.isEnabled,
            requiresExactAlarm = domain.requiresExactAlarm,
            enableSound = domain.enableSound,
            enableVibration = domain.enableVibration,
            autoRemoveAfterMinutes = domain.autoRemoveAfterMinutes,
            completionScope = domain.completionScope.name
        )
    }

    fun snoozeToEntity(scheduleId: Long, snoozeUntilTimestamp: Long, activityId: String): ScheduleSnoozeEntity {
        return ScheduleSnoozeEntity(
            scheduleId = scheduleId,
            snoozeUntilTimestamp = snoozeUntilTimestamp,
            activityId = activityId
        )
    }
}

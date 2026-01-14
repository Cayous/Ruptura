package com.ruptura.app.domain.usecase.schedule

import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import javax.inject.Inject

class AddScheduleUseCase @Inject constructor(
    private val repository: SpiritualScheduleRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(schedule: SpiritualSchedule): Long {
        val scheduleId = repository.insertSchedule(schedule)
        val scheduleWithId = schedule.copy(id = scheduleId)
        if (scheduleWithId.isEnabled) {
            scheduler.schedule(scheduleWithId)
        }
        return scheduleId
    }
}

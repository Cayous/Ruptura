package com.ruptura.app.domain.usecase.schedule

import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val repository: SpiritualScheduleRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(schedule: SpiritualSchedule) {
        scheduler.cancel(schedule.id)
        repository.deleteSchedule(schedule)
    }
}

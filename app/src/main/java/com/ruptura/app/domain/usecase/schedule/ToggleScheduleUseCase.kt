package com.ruptura.app.domain.usecase.schedule

import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import javax.inject.Inject

class ToggleScheduleUseCase @Inject constructor(
    private val repository: SpiritualScheduleRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(scheduleId: Long, enabled: Boolean) {
        repository.toggleEnabled(scheduleId, enabled)
        val schedule = repository.getScheduleById(scheduleId)
        if (schedule != null) {
            if (enabled) {
                scheduler.schedule(schedule)
            } else {
                scheduler.cancel(scheduleId)
            }
        }
    }
}

package com.ruptura.app.domain.usecase.schedule

import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import javax.inject.Inject

class RescheduleAllUseCase @Inject constructor(
    private val repository: SpiritualScheduleRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke() {
        val enabledSchedules = repository.getEnabledSchedules()
        scheduler.rescheduleAll(enabledSchedules)

        // Reschedule active snoozes
        val currentTime = System.currentTimeMillis()
        val activeSnoozes = repository.getAllActiveSnoozes(currentTime)
        activeSnoozes.forEach { (scheduleId, timestamp) ->
            val schedule = repository.getScheduleById(scheduleId)
            if (schedule != null) {
                val delayMinutes = ((timestamp - currentTime) / 60000).toInt()
                if (delayMinutes > 0) {
                    scheduler.scheduleSnooze(scheduleId, schedule.activityId, delayMinutes)
                }
            }
        }

        // Cleanup expired snoozes
        repository.deleteExpiredSnoozes(currentTime)
    }
}

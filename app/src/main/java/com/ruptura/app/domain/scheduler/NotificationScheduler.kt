package com.ruptura.app.domain.scheduler

import com.ruptura.app.domain.model.SpiritualSchedule

interface NotificationScheduler {
    fun schedule(schedule: SpiritualSchedule)
    fun scheduleSnooze(scheduleId: Long, activityId: String, delayMinutes: Int)
    fun cancel(scheduleId: Long)
    fun rescheduleAll(schedules: List<SpiritualSchedule>)
}

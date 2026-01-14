package com.ruptura.app.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.scheduler.NotificationScheduler
import com.ruptura.app.notification.SpiritualNotificationReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : NotificationScheduler {

    companion object {
        private const val REQUEST_CODE_BASE = 10000
        private const val SNOOZE_REQUEST_CODE_BASE = 20000
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
        const val EXTRA_ACTIVITY_ID = "extra_activity_id"
        const val EXTRA_IS_SNOOZE = "extra_is_snooze"
    }

    override fun schedule(schedule: SpiritualSchedule) {
        if (!schedule.isEnabled) {
            cancel(schedule.id)
            return
        }

        val intent = Intent(context, SpiritualNotificationReceiver::class.java).apply {
            putExtra(EXTRA_SCHEDULE_ID, schedule.id)
            putExtra(EXTRA_ACTIVITY_ID, schedule.activityId)
            putExtra(EXTRA_IS_SNOOZE, false)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(schedule.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = calculateNextTriggerTime(schedule.minutesOfDay)

        // Check if we can schedule exact alarms
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        when {
            schedule.requiresExactAlarm && canScheduleExact -> {
                // Use exact alarm for critical schedules
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            else -> {
                // Use inexact alarm (tolerância de ~15min)
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    override fun scheduleSnooze(scheduleId: Long, activityId: String, delayMinutes: Int) {
        val intent = Intent(context, SpiritualNotificationReceiver::class.java).apply {
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(EXTRA_ACTIVITY_ID, activityId)
            putExtra(EXTRA_IS_SNOOZE, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getSnoozeRequestCode(scheduleId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        // Snoozes always use exact alarms when possible
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    override fun cancel(scheduleId: Long) {
        // Cancel regular alarm
        val intent = Intent(context, SpiritualNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(scheduleId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }

        // Cancel snooze alarm
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            getSnoozeRequestCode(scheduleId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        snoozePendingIntent?.let { alarmManager.cancel(it) }
    }

    override fun rescheduleAll(schedules: List<SpiritualSchedule>) {
        schedules.forEach { schedule ->
            schedule(schedule)
        }
    }

    private fun calculateNextTriggerTime(minutesOfDay: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
            set(Calendar.MINUTE, minutesOfDay % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    private fun getRequestCode(scheduleId: Long): Int {
        return REQUEST_CODE_BASE + scheduleId.toInt()
    }

    private fun getSnoozeRequestCode(scheduleId: Long): Int {
        return SNOOZE_REQUEST_CODE_BASE + scheduleId.toInt()
    }
}

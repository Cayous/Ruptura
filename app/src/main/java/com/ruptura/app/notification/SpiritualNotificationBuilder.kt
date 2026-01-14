package com.ruptura.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ruptura.app.MainActivity
import com.ruptura.app.R
import com.ruptura.app.domain.model.SpiritualActivity
import com.ruptura.app.domain.model.SpiritualSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpiritualNotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    companion object {
        const val CHANNEL_ID = "spiritual_reminders"
        private const val CHANNEL_NAME = "Lembretes Espirituais"
        private const val CHANNEL_DESCRIPTION = "Notificações para atos de piedade"

        const val ACTION_COMPLETE = "com.ruptura.app.ACTION_COMPLETE"
        const val ACTION_SNOOZE = "com.ruptura.app.ACTION_SNOOZE"
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
        const val EXTRA_ACTIVITY_ID = "extra_activity_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(
        schedule: SpiritualSchedule,
        activity: SpiritualActivity,
        notificationId: Int
    ): NotificationCompat.Builder {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "spiritual_life")
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(EXTRA_SCHEDULE_ID, schedule.id)
            putExtra(EXTRA_ACTIVITY_ID, activity.id)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_SCHEDULE_ID, schedule.id)
            putExtra(EXTRA_ACTIVITY_ID, activity.id)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(activity.name)
            .setContentText("Hora de realizar seu ato de piedade")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(false) // Persistent
            .setOngoing(false)
            .addAction(
                0,
                "Concluir",
                completePendingIntent
            )
            .addAction(
                0,
                "Adiar 10min",
                snoozePendingIntent
            )

        // Sound and vibration based on schedule settings
        if (schedule.enableSound) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        } else {
            builder.setSilent(true)
        }

        if (schedule.enableVibration) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        }

        // Auto-remove timeout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            schedule.autoRemoveAfterMinutes?.let { minutes ->
                builder.setTimeoutAfter((minutes * 60 * 1000).toLong())
            }
        }

        return builder
    }

    fun generateNotificationId(scheduleId: Long): Int {
        return (scheduleId * 1000 + (System.currentTimeMillis() % 1000)).toInt()
    }
}

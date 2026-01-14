package com.ruptura.app.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ruptura.app.domain.model.CompletionType
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import com.ruptura.app.domain.usecase.spiritual.MarkSpiritualActivityCompleteUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var markCompleteUseCase: MarkSpiritualActivityCompleteUseCase

    @Inject
    lateinit var scheduleRepository: SpiritualScheduleRepository

    @Inject
    lateinit var scheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra(SpiritualNotificationBuilder.EXTRA_SCHEDULE_ID, -1L)
        val activityId = intent.getStringExtra(SpiritualNotificationBuilder.EXTRA_ACTIVITY_ID) ?: return
        val notificationId = intent.getIntExtra(SpiritualNotificationBuilder.EXTRA_NOTIFICATION_ID, -1)

        if (scheduleId == -1L || notificationId == -1) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    SpiritualNotificationBuilder.ACTION_COMPLETE -> {
                        // Marcar como completo
                        markCompleteUseCase(
                            activityId = activityId,
                            completionType = com.ruptura.app.domain.model.CompletionType.MANUAL_CHECK
                        )

                        // Deletar snooze se existir
                        scheduleRepository.deleteSnooze(scheduleId)

                        // Descartar notificação
                        notificationManager.cancel(notificationId)
                    }

                    SpiritualNotificationBuilder.ACTION_SNOOZE -> {
                        // Persistir snooze no banco
                        val snoozeTimestamp = System.currentTimeMillis() + (10 * 60 * 1000) // +10 minutos
                        scheduleRepository.insertSnooze(scheduleId, snoozeTimestamp, activityId)

                        // Agendar alarme para +10 minutos
                        scheduler.scheduleSnooze(scheduleId, activityId, 10)

                        // Descartar notificação atual
                        notificationManager.cancel(notificationId)
                    }
                }

                pendingResult.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                pendingResult.finish()
            }
        }
    }
}

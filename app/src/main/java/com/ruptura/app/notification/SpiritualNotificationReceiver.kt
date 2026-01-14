package com.ruptura.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ruptura.app.data.scheduler.AndroidAlarmScheduler
import com.ruptura.app.domain.model.CompletionScope
import com.ruptura.app.domain.repository.SpiritualRepository
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class SpiritualNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduleRepository: SpiritualScheduleRepository

    @Inject
    lateinit var spiritualRepository: SpiritualRepository

    @Inject
    lateinit var notificationBuilder: SpiritualNotificationBuilder

    @Inject
    lateinit var scheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra(AndroidAlarmScheduler.EXTRA_SCHEDULE_ID, -1L)
        val activityId = intent.getStringExtra(AndroidAlarmScheduler.EXTRA_ACTIVITY_ID) ?: return
        val isSnooze = intent.getBooleanExtra(AndroidAlarmScheduler.EXTRA_IS_SNOOZE, false)

        if (scheduleId == -1L) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val schedule = scheduleRepository.getScheduleById(scheduleId)
                if (schedule == null || !schedule.isEnabled) {
                    pendingResult.finish()
                    return@launch
                }

                // Lógica inteligente: verificar se já foi completado
                val today = LocalDate.now().toString()
                val isCompleted = when (schedule.completionScope) {
                    CompletionScope.DAILY -> {
                        spiritualRepository.isActivityCompleted(activityId, today)
                    }
                    CompletionScope.PER_SCHEDULE -> {
                        // Feature futura: verificar se este horário específico foi completado
                        false
                    }
                }

                if (isCompleted) {
                    // Já completado, apenas reagendar para próximo dia
                    if (!isSnooze) {
                        scheduler.schedule(schedule)
                    }
                    pendingResult.finish()
                    return@launch
                }

                // Buscar atividade para exibir na notificação
                val activities = spiritualRepository.getAllActivities()
                val activity = activities.find { it.id == activityId }

                if (activity != null) {
                    // Gerar notificationId dinâmico
                    val notificationId = notificationBuilder.generateNotificationId(scheduleId)

                    // Criar e exibir notificação
                    val notification = notificationBuilder.buildNotification(
                        schedule,
                        activity,
                        notificationId
                    ).build()

                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as android.app.NotificationManager
                    notificationManager.notify(notificationId, notification)
                }

                // Reagendar para próximo dia (apenas se não for snooze)
                if (!isSnooze) {
                    scheduler.schedule(schedule)
                }

                // Se foi snooze, deletar do banco
                if (isSnooze) {
                    scheduleRepository.deleteSnooze(scheduleId)
                }

                pendingResult.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                pendingResult.finish()
            }
        }
    }
}

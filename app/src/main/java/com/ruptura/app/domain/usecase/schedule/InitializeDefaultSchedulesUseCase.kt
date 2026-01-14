package com.ruptura.app.domain.usecase.schedule

import android.content.Context
import android.content.SharedPreferences
import com.ruptura.app.domain.model.CompletionScope
import com.ruptura.app.domain.model.PeriodOfDay
import com.ruptura.app.domain.model.SpiritualSchedule
import com.ruptura.app.domain.repository.SpiritualScheduleRepository
import com.ruptura.app.domain.scheduler.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InitializeDefaultSchedulesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SpiritualScheduleRepository,
    private val scheduler: NotificationScheduler,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_HAS_INITIALIZED = "has_initialized_default_schedules"

        // Activity IDs mapping from existing activities
        private const val OFERECIMENTO_OBRAS_ID = "oferecimento_obras"
        private const val ORACAO_MANHA_ID = "oracao_manha"
        private const val SANTA_MISSA_ID = "santa_missa"
        private const val LEITURA_NOVO_TESTAMENTO_ID = "leitura_novo_testamento"
        private const val CONTEMPLAR_ROSARIO_ID = "contemplar_rosario"
        private const val ANGELUS_ID = "angelus_regina_coeli"
        private const val PRECES_ID = "preces"
        private const val VISITA_SANTISSIMO_ID = "visita_santissimo"
        private const val ORACAO_TARDE_ID = "oracao_tarde"
        private const val SANTO_ROSARIO_ID = "santo_rosario"
        private const val LEITURA_ESPIRITUAL_ID = "leitura_espiritual"
        private const val EXAME_CONSCIENCIA_ID = "exame_consciencia"
        private const val TRES_AVE_MARIAS_ID = "tres_ave_marias"
        private const val LEMBRAI_VOS_ID = "lembrai_vos"
    }

    suspend operator fun invoke(force: Boolean = false) {
        val hasInitialized = sharedPreferences.getBoolean(KEY_HAS_INITIALIZED, false)
        if (hasInitialized && !force) {
            return
        }

        val defaultSchedules = listOf(
            // 🌅 MANHÃ
            SpiritualSchedule(
                activityId = OFERECIMENTO_OBRAS_ID,
                minutesOfDay = 6 * 60 + 30, // 06:30
                periodOfDay = PeriodOfDay.MORNING,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = ORACAO_MANHA_ID,
                minutesOfDay = 6 * 60 + 31, // 06:31
                periodOfDay = PeriodOfDay.MORNING,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = SANTA_MISSA_ID,
                minutesOfDay = 7 * 60, // 07:00
                periodOfDay = PeriodOfDay.MORNING,
                requiresExactAlarm = true, // Horário crítico
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = LEITURA_NOVO_TESTAMENTO_ID,
                minutesOfDay = 7 * 60 + 30, // 07:30
                periodOfDay = PeriodOfDay.MORNING,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = CONTEMPLAR_ROSARIO_ID,
                minutesOfDay = 7 * 60 + 35, // 07:35
                periodOfDay = PeriodOfDay.MORNING,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),

            // ☀️ TARDE
            SpiritualSchedule(
                activityId = ANGELUS_ID,
                minutesOfDay = 12 * 60, // 12:00
                periodOfDay = PeriodOfDay.AFTERNOON,
                requiresExactAlarm = true, // Horário crítico
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = PRECES_ID,
                minutesOfDay = 12 * 60 + 1, // 12:01
                periodOfDay = PeriodOfDay.AFTERNOON,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = VISITA_SANTISSIMO_ID,
                minutesOfDay = 17 * 60 + 30, // 17:30
                periodOfDay = PeriodOfDay.AFTERNOON,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = ORACAO_TARDE_ID,
                minutesOfDay = 17 * 60 + 35, // 17:35
                periodOfDay = PeriodOfDay.AFTERNOON,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),

            // 🌙 NOITE
            SpiritualSchedule(
                activityId = SANTO_ROSARIO_ID,
                minutesOfDay = 19 * 60 + 30, // 19:30
                periodOfDay = PeriodOfDay.NIGHT,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = LEITURA_ESPIRITUAL_ID,
                minutesOfDay = 20 * 60, // 20:00
                periodOfDay = PeriodOfDay.NIGHT,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = EXAME_CONSCIENCIA_ID,
                minutesOfDay = 22 * 60, // 22:00
                periodOfDay = PeriodOfDay.NIGHT,
                requiresExactAlarm = true, // Horário crítico
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = TRES_AVE_MARIAS_ID,
                minutesOfDay = 22 * 60 + 5, // 22:05
                periodOfDay = PeriodOfDay.NIGHT,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            ),
            SpiritualSchedule(
                activityId = LEMBRAI_VOS_ID,
                minutesOfDay = 22 * 60 + 6, // 22:06
                periodOfDay = PeriodOfDay.NIGHT,
                requiresExactAlarm = false,
                completionScope = CompletionScope.DAILY
            )
        )

        // Insert all schedules
        repository.insertSchedules(defaultSchedules)

        // Schedule all alarms
        val insertedSchedules = repository.getEnabledSchedules()
        scheduler.rescheduleAll(insertedSchedules)

        // Mark as initialized
        sharedPreferences.edit().putBoolean(KEY_HAS_INITIALIZED, true).apply()
    }
}

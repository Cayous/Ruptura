package com.ruptura.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ruptura.app.domain.usecase.schedule.RescheduleAllUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var rescheduleAllUseCase: RescheduleAllUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Reagendar todos os alarmes habilitados
                // e snoozes ativos, limpar snoozes expirados
                rescheduleAllUseCase()

                pendingResult.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                pendingResult.finish()
            }
        }
    }
}

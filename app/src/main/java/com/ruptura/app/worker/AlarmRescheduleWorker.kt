package com.ruptura.app.worker

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ruptura.app.domain.usecase.schedule.RescheduleAllUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class AlarmRescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val rescheduleAllUseCase: RescheduleAllUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            rescheduleAllUseCase()
            Result.success()
        } catch (e: Exception) {
            // Retry para erros temporários (I/O, storage)
            // Failure para erros lógicos/dados inválidos
            if (e is IOException || e is SQLiteException) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val WORK_NAME = "alarm_reschedule_work"
    }
}

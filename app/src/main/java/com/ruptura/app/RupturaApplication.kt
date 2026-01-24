package com.ruptura.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ruptura.app.domain.usecase.schedule.InitializeDefaultSchedulesUseCase
import com.ruptura.app.worker.AlarmRescheduleWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class RupturaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var initializeDefaultSchedulesUseCase: InitializeDefaultSchedulesUseCase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        scheduleAlarmRescheduleWork()

        // Initialize default schedules on first launch
        applicationScope.launch {
            try {
                initializeDefaultSchedulesUseCase()
            } catch (e: Exception) {
                // Log error but don't crash the app
                e.printStackTrace()
            }
        }
    }

    private fun scheduleAlarmRescheduleWork() {
        val workRequest = PeriodicWorkRequestBuilder<AlarmRescheduleWorker>(
            6, TimeUnit.HOURS
        ).setInitialDelay(6, TimeUnit.HOURS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.MINUTES
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AlarmRescheduleWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

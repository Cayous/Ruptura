package com.ruptura.app

import android.app.Application
import com.ruptura.app.domain.usecase.schedule.InitializeDefaultSchedulesUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RupturaApplication : Application() {

    @Inject
    lateinit var initializeDefaultSchedulesUseCase: InitializeDefaultSchedulesUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

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
}

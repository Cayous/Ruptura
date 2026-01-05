package com.ruptura.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ruptura.app.MainActivity
import com.ruptura.app.R
import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionConfig
import com.ruptura.app.domain.repository.FocusSessionRepository
import com.ruptura.app.domain.usecase.session.CompleteSessionUseCase
import com.ruptura.app.domain.usecase.session.UpdateSessionPhaseUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusLockService : LifecycleService(), SavedStateRegistryOwner {

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    companion object {
        const val ACTION_START_SESSION = "START_SESSION"
        const val ACTION_END_SESSION = "END_SESSION"
        const val ACTION_EMERGENCY_EXIT = "EMERGENCY_EXIT"

        const val EXTRA_SESSION_CONFIG = "SESSION_CONFIG"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "focus_lock_channel"
    }

    @Inject
    lateinit var windowManager: WindowManager

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: FocusSessionRepository

    @Inject
    lateinit var updatePhaseUseCase: UpdateSessionPhaseUseCase

    @Inject
    lateinit var completeSessionUseCase: CompleteSessionUseCase

    private var overlayView: View? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentSession: FocusSession? = null
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_START_SESSION -> {
                val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_SESSION_CONFIG, SessionConfig::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_SESSION_CONFIG)
                }
                config?.let { startSession(it) }
            }
            ACTION_END_SESSION -> endSession()
            ACTION_EMERGENCY_EXIT -> handleEmergencyExit()
        }

        return START_STICKY
    }

    private fun startSession(config: SessionConfig) {
        android.util.Log.d("FocusLockService", "=== startSession called ===")
        android.util.Log.d("FocusLockService", "Config: focusDuration=${config.focusDurationMinutes}min")

        lifecycleScope.launch {
            try {
                android.util.Log.d("FocusLockService", "Creating session in repository...")
                val session = repository.createSession(config)
                android.util.Log.d("FocusLockService", "✓ Session created: id=${session.id}")
                currentSession = session

                // Start foreground with notification
                android.util.Log.d("FocusLockService", "Starting foreground service...")
                startForeground(NOTIFICATION_ID, createNotification(session))
                android.util.Log.d("FocusLockService", "✓ Foreground service started")

                // Show overlay for focus phase
                android.util.Log.d("FocusLockService", "Showing lock overlay...")
                showLockOverlay(session)
                android.util.Log.d("FocusLockService", "✓ Lock overlay shown")

                // Start timer
                android.util.Log.d("FocusLockService", "Starting phase timer...")
                startPhaseTimer(session)
                android.util.Log.d("FocusLockService", "✓ Timer started")

                // Acquire wake lock
                android.util.Log.d("FocusLockService", "Acquiring wake lock...")
                acquireWakeLock()
                android.util.Log.d("FocusLockService", "✓ Wake lock acquired")

                android.util.Log.d("FocusLockService", "=== Session started successfully ===")
            } catch (e: Exception) {
                android.util.Log.e("FocusLockService", "❌ FAILED to start session", e)
                android.util.Log.e("FocusLockService", "Exception: ${e.message}")
                android.widget.Toast.makeText(this@FocusLockService, "Erro ao iniciar sessão: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                stopSelf()
            }
        }
    }

    private fun startPhaseTimer(session: FocusSession) {
        timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            val duration = session.phaseEndTime - session.phaseStartTime

            flow {
                var elapsed = 0L
                while (elapsed <= duration) {
                    val remaining = duration - elapsed
                    emit(remaining)
                    delay(1000)
                    elapsed += 1000
                }
            }.collect { remaining ->
                // Update notification with remaining time
                updateNotification(session, remaining)

                // Phase complete
                if (remaining <= 0) {
                    handlePhaseComplete(session)
                }
            }
        }
    }

    private suspend fun handlePhaseComplete(session: FocusSession) {
        // For MVP: just complete the session
        completeSession(session)
    }

    private suspend fun completeSession(session: FocusSession) {
        completeSessionUseCase(session)
        hideLockOverlay()
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun endSession() {
        lifecycleScope.launch {
            currentSession?.let { session ->
                completeSession(session)
            }
        }
    }

    private fun handleEmergencyExit() {
        endSession()
    }

    private fun showLockOverlay(session: FocusSession) {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            )
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            )
        }

        // Create ComposeView programmatically (no XML needed)
        val composeView = ComposeView(this).apply {
            // Use DisposeOnDetachedFromWindow strategy to avoid SavedStateRegistry requirement
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

            // Set lifecycle owner and saved state registry owner
            setViewTreeLifecycleOwner(this@FocusLockService)
            setViewTreeSavedStateRegistryOwner(this@FocusLockService)

            // Set Compose content
            setContent {
                var remainingTimeState by remember {
                    mutableStateOf(session.phaseEndTime - System.currentTimeMillis())
                }

                LaunchedEffect(Unit) {
                    while (remainingTimeState > 0) {
                        delay(1000)
                        remainingTimeState = (session.phaseEndTime - System.currentTimeMillis()).coerceAtLeast(0)
                    }
                }

                com.ruptura.app.presentation.theme.RupturaTheme {
                    com.ruptura.app.presentation.focus.lock.LockScreenContent(
                        session = session,
                        remainingTime = remainingTimeState,
                        onEmergencyExit = { handleEmergencyExit() }
                    )
                }
            }
        }

        overlayView = composeView

        try {
            android.util.Log.d("FocusLockService", "=== Starting overlay creation ===")
            android.util.Log.d("FocusLockService", "Overlay view: $overlayView")
            android.util.Log.d("FocusLockService", "Window params: $params")

            // Check overlay permission explicitly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val canDraw = Settings.canDrawOverlays(this)
                android.util.Log.d("FocusLockService", "Can draw overlays: $canDraw")
                if (!canDraw) {
                    android.util.Log.e("FocusLockService", "OVERLAY PERMISSION NOT GRANTED!")
                    // Show user-friendly error
                    android.widget.Toast.makeText(this, "Permissão de overlay necessária", android.widget.Toast.LENGTH_LONG).show()
                    stopSelf()
                    return
                }
            }

            android.util.Log.d("FocusLockService", "Adding view to window manager...")
            windowManager.addView(overlayView, params)
            android.util.Log.d("FocusLockService", "✓ Overlay added successfully")

            android.util.Log.d("FocusLockService", "Enabling immersive mode...")
            enableImmersiveMode()
            android.util.Log.d("FocusLockService", "✓ Immersive mode enabled")

        } catch (e: Exception) {
            android.util.Log.e("FocusLockService", "❌ FAILED to create overlay", e)
            android.util.Log.e("FocusLockService", "Exception type: ${e.javaClass.name}")
            android.util.Log.e("FocusLockService", "Exception message: ${e.message}")
            e.printStackTrace()

            // Show user-friendly error
            android.widget.Toast.makeText(this, "Erro ao criar overlay: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun hideLockOverlay() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // View already removed
            }
            overlayView = null
        }
    }

    @Suppress("DEPRECATION")
    private fun enableImmersiveMode() {
        overlayView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ZenApp::FocusLock"
            )
        }

        // Calculate wake lock duration based on session duration
        val session = currentSession
        val wakeLockDuration = if (session != null) {
            // Session duration + 2 minute buffer for cleanup
            (session.phaseEndTime - session.phaseStartTime) + (2 * 60 * 1000L)
        } else {
            // Fallback: 2 hours if no session info available
            2 * 60 * 60 * 1000L
        }

        wakeLock?.acquire(wakeLockDuration)
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active focus session status"
                setSound(null, null)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(session: FocusSession): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foco ativo")
            .setContentText("Sessão de foco em andamento")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(session: FocusSession, remainingMillis: Long) {
        val minutes = (remainingMillis / 1000 / 60).toInt()
        val seconds = (remainingMillis / 1000 % 60).toInt()
        val timeString = String.format("%02d:%02d", minutes, seconds)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foco ativo")
            .setContentText("Restam $timeString")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        hideLockOverlay()
        releaseWakeLock()
    }
}

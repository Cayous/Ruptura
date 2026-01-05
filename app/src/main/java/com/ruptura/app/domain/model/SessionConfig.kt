package com.ruptura.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionConfig(
    val focusDurationMinutes: Int,      // 25, 30, 45, 60
    val breakDurationMinutes: Int,      // 5, 10, 15
    val totalCycles: Int,               // 1, 2, 4, or -1 for "until I stop"
    val monkModeEnabled: Boolean = false,
    val emergencyExitType: EmergencyExitType = EmergencyExitType.HOLD_AND_TYPE,
    val spiritualActivityId: String? = null  // Links to spiritual activity if session started from one
) : Parcelable

enum class EmergencyExitType {
    HOLD_AND_TYPE,      // Hold 10s + type phrase
    MONK_MODE           // 60s wait + phrase (only after time expires or with 60s wait)
}

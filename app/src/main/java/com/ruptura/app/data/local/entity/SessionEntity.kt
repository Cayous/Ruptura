package com.ruptura.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Config
    val focusDurationMinutes: Int,
    val breakDurationMinutes: Int,
    val totalCycles: Int,
    val monkModeEnabled: Boolean,
    val emergencyExitType: String,

    // State
    val state: String,
    val currentCycle: Int,
    val currentPhase: String,
    val phaseStartTime: Long,
    val phaseEndTime: Long,
    val totalFocusTimeMillis: Long,
    val breakCount: Int,
    val createdAt: Long,

    // Spiritual Activity Link
    val spiritualActivityId: String? = null
)

package com.ruptura.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session_stats",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("createdAt")]
)
data class SessionStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val totalFocusTimeMillis: Long,
    val completedCycles: Int,
    val plannedCycles: Int,
    val breakCount: Int,
    val sessionDurationMillis: Long,
    val createdAt: Long,
    val completedAt: Long?
)

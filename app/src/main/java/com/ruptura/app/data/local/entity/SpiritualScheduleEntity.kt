package com.ruptura.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spiritual_schedules",
    foreignKeys = [
        ForeignKey(
            entity = SpiritualActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("activityId")]
)
data class SpiritualScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: String,
    val minutesOfDay: Int, // 0-1439 (0 = 00:00, 1439 = 23:59)
    val periodOfDay: String, // MORNING, AFTERNOON, NIGHT
    val isEnabled: Boolean = true,
    val requiresExactAlarm: Boolean = false,
    val enableSound: Boolean = true,
    val enableVibration: Boolean = true,
    val autoRemoveAfterMinutes: Int? = null, // null = persistente
    val completionScope: String = "DAILY" // DAILY, PER_SCHEDULE
)

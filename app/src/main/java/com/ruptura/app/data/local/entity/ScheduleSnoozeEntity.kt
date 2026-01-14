package com.ruptura.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_snoozes",
    foreignKeys = [
        ForeignKey(
            entity = SpiritualScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scheduleId"), Index("activityId")]
)
data class ScheduleSnoozeEntity(
    @PrimaryKey
    val scheduleId: Long,
    val snoozeUntilTimestamp: Long,
    val activityId: String
)

package com.ruptura.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "spiritual_completions",
    primaryKeys = ["activityId", "completionDate"],
    foreignKeys = [
        ForeignKey(
            entity = SpiritualActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("completionDate")]
)
data class SpiritualCompletionEntity(
    val activityId: String,
    val completionDate: String,
    val completedAt: Long,
    val completionType: String,
    val sessionId: Long?
)

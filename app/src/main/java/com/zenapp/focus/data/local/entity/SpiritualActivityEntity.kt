package com.zenapp.focus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spiritual_activities")
data class SpiritualActivityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val orderIndex: Int
)

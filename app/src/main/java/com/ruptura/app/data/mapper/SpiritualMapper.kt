package com.ruptura.app.data.mapper

import com.ruptura.app.data.local.entity.SpiritualActivityEntity
import com.ruptura.app.data.local.entity.SpiritualCompletionEntity
import com.ruptura.app.domain.model.CompletionType
import com.ruptura.app.domain.model.SpiritualActivity
import com.ruptura.app.domain.model.SpiritualCompletion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpiritualMapper @Inject constructor() {

    fun toDomain(entity: SpiritualActivityEntity): SpiritualActivity {
        return SpiritualActivity(
            id = entity.id,
            name = entity.name,
            durationSeconds = entity.durationSeconds,
            orderIndex = entity.orderIndex,
            allowsTimeSelection = entity.allowsTimeSelection,
            availableDurations = if (entity.availableDurations.isNotEmpty()) {
                entity.availableDurations.split(",").map { it.toInt() }
            } else {
                emptyList()
            }
        )
    }

    fun toEntity(domain: SpiritualActivity): SpiritualActivityEntity {
        return SpiritualActivityEntity(
            id = domain.id,
            name = domain.name,
            durationSeconds = domain.durationSeconds,
            orderIndex = domain.orderIndex,
            allowsTimeSelection = domain.allowsTimeSelection,
            availableDurations = domain.availableDurations.joinToString(",")
        )
    }

    fun completionToDomain(entity: SpiritualCompletionEntity): SpiritualCompletion {
        return SpiritualCompletion(
            activityId = entity.activityId,
            completionDate = entity.completionDate,
            completedAt = entity.completedAt,
            completionType = CompletionType.valueOf(entity.completionType),
            sessionId = entity.sessionId
        )
    }

    fun completionToEntity(domain: SpiritualCompletion): SpiritualCompletionEntity {
        return SpiritualCompletionEntity(
            activityId = domain.activityId,
            completionDate = domain.completionDate,
            completedAt = domain.completedAt,
            completionType = domain.completionType.name,
            sessionId = domain.sessionId
        )
    }
}

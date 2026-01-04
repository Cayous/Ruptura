package com.zenapp.focus.data.mapper

import com.zenapp.focus.data.local.entity.SpiritualActivityEntity
import com.zenapp.focus.data.local.entity.SpiritualCompletionEntity
import com.zenapp.focus.domain.model.CompletionType
import com.zenapp.focus.domain.model.SpiritualActivity
import com.zenapp.focus.domain.model.SpiritualCompletion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpiritualMapper @Inject constructor() {

    fun toDomain(entity: SpiritualActivityEntity): SpiritualActivity {
        return SpiritualActivity(
            id = entity.id,
            name = entity.name,
            durationSeconds = entity.durationSeconds,
            orderIndex = entity.orderIndex
        )
    }

    fun toEntity(domain: SpiritualActivity): SpiritualActivityEntity {
        return SpiritualActivityEntity(
            id = domain.id,
            name = domain.name,
            durationSeconds = domain.durationSeconds,
            orderIndex = domain.orderIndex
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

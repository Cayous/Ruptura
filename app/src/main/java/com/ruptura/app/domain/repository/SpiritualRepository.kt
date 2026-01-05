package com.ruptura.app.domain.repository

import com.ruptura.app.domain.model.CompletionType
import com.ruptura.app.domain.model.SpiritualActivity
import com.ruptura.app.domain.model.SpiritualActivityWithStatus

interface SpiritualRepository {
    suspend fun getAllActivities(): List<SpiritualActivity>
    suspend fun initializeActivities()
    suspend fun getActivitiesWithStatusForDate(date: String): List<SpiritualActivityWithStatus>
    suspend fun getTodayActivitiesWithStatus(): List<SpiritualActivityWithStatus>
    suspend fun markActivityComplete(
        activityId: String,
        date: String,
        completionType: CompletionType,
        sessionId: Long? = null
    )
    suspend fun isActivityCompleted(activityId: String, date: String): Boolean
    suspend fun cleanupOldCompletions(daysToKeep: Int = 90)
}

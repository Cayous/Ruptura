package com.ruptura.app.domain.repository

import com.ruptura.app.domain.model.DailySessionStats
import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionConfig
import com.ruptura.app.domain.model.SessionStats

interface FocusSessionRepository {
    // Active session management
    suspend fun createSession(config: SessionConfig): FocusSession
    suspend fun getActiveSession(): FocusSession?
    suspend fun updateSession(session: FocusSession)
    suspend fun completeSession(sessionId: Long, stats: SessionStats)
    suspend fun cancelSession(sessionId: Long)

    // Session state queries
    suspend fun getSession(sessionId: Long): FocusSession?
    suspend fun getAllSessions(): List<FocusSession>

    // Stats queries
    suspend fun getSessionStats(sessionId: Long): SessionStats?
    suspend fun getDailyStats(startDate: String, endDate: String): List<DailySessionStats>
    suspend fun getTodayStats(): DailySessionStats?

    // Cache management
    fun invalidateCache()
}

package com.ruptura.app.data.repository

import com.ruptura.app.data.local.dao.SessionDao
import com.ruptura.app.data.local.dao.SessionStatsDao
import com.ruptura.app.data.mapper.SessionMapper
import com.ruptura.app.domain.model.DailySessionStats
import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionConfig
import com.ruptura.app.domain.model.SessionPhase
import com.ruptura.app.domain.model.SessionState
import com.ruptura.app.domain.model.SessionStats
import com.ruptura.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val statsDao: SessionStatsDao,
    private val mapper: SessionMapper
) : FocusSessionRepository {

    private var activeSessionCache: FocusSession? = null

    override suspend fun createSession(config: SessionConfig): FocusSession =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val focusDuration = config.focusDurationMinutes * 60 * 1000L

            val session = FocusSession(
                id = 0,
                config = config,
                state = SessionState.FOCUS_ACTIVE,
                currentCycle = 1,
                currentPhase = SessionPhase.FOCUS,
                phaseStartTime = now,
                phaseEndTime = now + focusDuration,
                totalFocusTimeMillis = 0,
                breakCount = 0,
                createdAt = now,
                spiritualActivityId = config.spiritualActivityId
            )

            val entity = mapper.toEntity(session)
            val id = sessionDao.insertSession(entity)
            val created = session.copy(id = id)

            activeSessionCache = created
            created
        }

    override suspend fun getActiveSession(): FocusSession? =
        withContext(Dispatchers.IO) {
            // Return cached if available
            activeSessionCache?.let { return@withContext it }

            // Otherwise query DB
            val entity = sessionDao.getActiveSession()
            val session = entity?.let { mapper.toDomain(it) }
            activeSessionCache = session
            session
        }

    override suspend fun updateSession(session: FocusSession) =
        withContext(Dispatchers.IO) {
            val entity = mapper.toEntity(session)
            sessionDao.updateSession(entity)
            activeSessionCache = session
        }

    override suspend fun completeSession(sessionId: Long, stats: SessionStats) =
        withContext(Dispatchers.IO) {
            val session = getSession(sessionId)
            if (session != null) {
                val updated = session.copy(state = SessionState.COMPLETED)
                updateSession(updated)

                // CRITICAL: Wait for database write to complete
                // This ensures the next getActiveSession() sees the updated state
                kotlinx.coroutines.delay(100)  // Small delay to ensure DB write completes
            }

            val statsEntity = mapper.statsToEntity(stats)
            statsDao.insertStats(statsEntity)

            // Now it's safe to clear the cache
            activeSessionCache = null
        }

    override suspend fun cancelSession(sessionId: Long) =
        withContext(Dispatchers.IO) {
            val session = getSession(sessionId)
            if (session != null) {
                val updated = session.copy(state = SessionState.CANCELLED)
                updateSession(updated)
            }
            activeSessionCache = null
        }

    override suspend fun getSession(sessionId: Long): FocusSession? =
        withContext(Dispatchers.IO) {
            sessionDao.getSession(sessionId)?.let { mapper.toDomain(it) }
        }

    override suspend fun getAllSessions(): List<FocusSession> =
        withContext(Dispatchers.IO) {
            sessionDao.getAllSessions().map { mapper.toDomain(it) }
        }

    override suspend fun getSessionStats(sessionId: Long): SessionStats? =
        withContext(Dispatchers.IO) {
            statsDao.getStatsForSession(sessionId)?.let { mapper.statsToDomain(it) }
        }

    override suspend fun getDailyStats(startDate: String, endDate: String): List<DailySessionStats> =
        withContext(Dispatchers.IO) {
            statsDao.getDailyStats(startDate, endDate).map { mapper.dailyStatsToDomain(it) }
        }

    override suspend fun getTodayStats(): DailySessionStats? =
        withContext(Dispatchers.IO) {
            statsDao.getTodayStats()?.let { mapper.dailyStatsToDomain(it) }
        }

    override fun invalidateCache() {
        activeSessionCache = null
    }
}

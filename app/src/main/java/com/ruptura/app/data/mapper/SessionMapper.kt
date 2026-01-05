package com.ruptura.app.data.mapper

import com.ruptura.app.data.local.dao.DailyStatsResult
import com.ruptura.app.data.local.entity.SessionEntity
import com.ruptura.app.data.local.entity.SessionStatsEntity
import com.ruptura.app.domain.model.DailySessionStats
import com.ruptura.app.domain.model.EmergencyExitType
import com.ruptura.app.domain.model.FocusSession
import com.ruptura.app.domain.model.SessionConfig
import com.ruptura.app.domain.model.SessionPhase
import com.ruptura.app.domain.model.SessionState
import com.ruptura.app.domain.model.SessionStats
import javax.inject.Inject

class SessionMapper @Inject constructor() {

    fun toEntity(session: FocusSession): SessionEntity {
        return SessionEntity(
            id = session.id,
            focusDurationMinutes = session.config.focusDurationMinutes,
            breakDurationMinutes = session.config.breakDurationMinutes,
            totalCycles = session.config.totalCycles,
            monkModeEnabled = session.config.monkModeEnabled,
            emergencyExitType = session.config.emergencyExitType.name,
            state = session.state.name,
            currentCycle = session.currentCycle,
            currentPhase = session.currentPhase.name,
            phaseStartTime = session.phaseStartTime,
            phaseEndTime = session.phaseEndTime,
            totalFocusTimeMillis = session.totalFocusTimeMillis,
            breakCount = session.breakCount,
            createdAt = session.createdAt,
            spiritualActivityId = session.spiritualActivityId
        )
    }

    fun toDomain(entity: SessionEntity): FocusSession {
        return FocusSession(
            id = entity.id,
            config = SessionConfig(
                focusDurationMinutes = entity.focusDurationMinutes,
                breakDurationMinutes = entity.breakDurationMinutes,
                totalCycles = entity.totalCycles,
                monkModeEnabled = entity.monkModeEnabled,
                emergencyExitType = EmergencyExitType.valueOf(entity.emergencyExitType),
                spiritualActivityId = entity.spiritualActivityId
            ),
            state = SessionState.valueOf(entity.state),
            currentCycle = entity.currentCycle,
            currentPhase = SessionPhase.valueOf(entity.currentPhase),
            phaseStartTime = entity.phaseStartTime,
            phaseEndTime = entity.phaseEndTime,
            totalFocusTimeMillis = entity.totalFocusTimeMillis,
            breakCount = entity.breakCount,
            createdAt = entity.createdAt,
            spiritualActivityId = entity.spiritualActivityId
        )
    }

    fun statsToEntity(stats: SessionStats): SessionStatsEntity {
        return SessionStatsEntity(
            sessionId = stats.sessionId,
            totalFocusTimeMillis = stats.totalFocusTimeMillis,
            completedCycles = stats.completedCycles,
            plannedCycles = stats.plannedCycles,
            breakCount = stats.breakCount,
            sessionDurationMillis = stats.sessionDurationMillis,
            createdAt = stats.createdAt,
            completedAt = stats.completedAt
        )
    }

    fun statsToDomain(entity: SessionStatsEntity): SessionStats {
        return SessionStats(
            sessionId = entity.sessionId,
            totalFocusTimeMillis = entity.totalFocusTimeMillis,
            completedCycles = entity.completedCycles,
            plannedCycles = entity.plannedCycles,
            breakCount = entity.breakCount,
            sessionDurationMillis = entity.sessionDurationMillis,
            createdAt = entity.createdAt,
            completedAt = entity.completedAt
        )
    }

    fun dailyStatsToDomain(result: DailyStatsResult): DailySessionStats {
        return DailySessionStats(
            date = result.date,
            totalFocusTimeMillis = result.totalFocusTimeMillis,
            totalBreakTimeMillis = result.totalBreakTimeMillis,
            sessionsCompleted = result.sessionsCompleted,
            totalCycles = result.totalCycles,
            averageBreakCount = result.averageBreakCount
        )
    }
}

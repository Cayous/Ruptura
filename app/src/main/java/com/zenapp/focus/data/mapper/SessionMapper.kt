package com.zenapp.focus.data.mapper

import com.zenapp.focus.data.local.dao.DailyStatsResult
import com.zenapp.focus.data.local.entity.SessionEntity
import com.zenapp.focus.data.local.entity.SessionStatsEntity
import com.zenapp.focus.domain.model.DailySessionStats
import com.zenapp.focus.domain.model.EmergencyExitType
import com.zenapp.focus.domain.model.FocusSession
import com.zenapp.focus.domain.model.SessionConfig
import com.zenapp.focus.domain.model.SessionPhase
import com.zenapp.focus.domain.model.SessionState
import com.zenapp.focus.domain.model.SessionStats
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
            createdAt = session.createdAt
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
                emergencyExitType = EmergencyExitType.valueOf(entity.emergencyExitType)
            ),
            state = SessionState.valueOf(entity.state),
            currentCycle = entity.currentCycle,
            currentPhase = SessionPhase.valueOf(entity.currentPhase),
            phaseStartTime = entity.phaseStartTime,
            phaseEndTime = entity.phaseEndTime,
            totalFocusTimeMillis = entity.totalFocusTimeMillis,
            breakCount = entity.breakCount,
            createdAt = entity.createdAt
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

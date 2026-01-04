package com.zenapp.focus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zenapp.focus.data.local.entity.SessionStatsEntity

@Dao
interface SessionStatsDao {
    @Insert
    suspend fun insertStats(stats: SessionStatsEntity): Long

    @Query("SELECT * FROM session_stats WHERE sessionId = :sessionId")
    suspend fun getStatsForSession(sessionId: Long): SessionStatsEntity?

    @Query("""
        SELECT
            DATE(createdAt/1000, 'unixepoch') as date,
            SUM(totalFocusTimeMillis) as totalFocusTimeMillis,
            SUM(sessionDurationMillis - totalFocusTimeMillis) as totalBreakTimeMillis,
            COUNT(*) as sessionsCompleted,
            SUM(completedCycles) as totalCycles,
            AVG(breakCount) as averageBreakCount
        FROM session_stats
        WHERE DATE(createdAt/1000, 'unixepoch') BETWEEN :startDate AND :endDate
        GROUP BY DATE(createdAt/1000, 'unixepoch')
        ORDER BY date DESC
    """)
    suspend fun getDailyStats(startDate: String, endDate: String): List<DailyStatsResult>

    @Query("""
        SELECT
            DATE('now') as date,
            COALESCE(SUM(totalFocusTimeMillis), 0) as totalFocusTimeMillis,
            COALESCE(SUM(sessionDurationMillis - totalFocusTimeMillis), 0) as totalBreakTimeMillis,
            COUNT(*) as sessionsCompleted,
            COALESCE(SUM(completedCycles), 0) as totalCycles,
            COALESCE(AVG(breakCount), 0) as averageBreakCount
        FROM session_stats
        WHERE DATE(createdAt/1000, 'unixepoch') = DATE('now')
    """)
    suspend fun getTodayStats(): DailyStatsResult?
}

data class DailyStatsResult(
    val date: String,
    val totalFocusTimeMillis: Long,
    val totalBreakTimeMillis: Long,
    val sessionsCompleted: Int,
    val totalCycles: Int,
    val averageBreakCount: Float
)

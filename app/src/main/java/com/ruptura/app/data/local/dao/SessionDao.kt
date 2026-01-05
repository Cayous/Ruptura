package com.ruptura.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ruptura.app.data.local.entity.SessionEntity

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: Long): SessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE state IN ('FOCUS_ACTIVE', 'BREAK_ACTIVE', 'PAUSED') LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("SELECT * FROM focus_sessions ORDER BY createdAt DESC")
    suspend fun getAllSessions(): List<SessionEntity>

    @Query("DELETE FROM focus_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}

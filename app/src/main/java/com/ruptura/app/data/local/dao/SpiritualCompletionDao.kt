package com.ruptura.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruptura.app.data.local.entity.SpiritualCompletionEntity

@Dao
interface SpiritualCompletionDao {
    @Query("""
        SELECT * FROM spiritual_completions
        WHERE completionDate = :date
        ORDER BY completedAt DESC
    """)
    suspend fun getCompletionsForDate(date: String): List<SpiritualCompletionEntity>

    @Query("SELECT * FROM spiritual_completions WHERE activityId = :activityId AND completionDate = :date")
    suspend fun getCompletion(activityId: String, date: String): SpiritualCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: SpiritualCompletionEntity)

    @Query("DELETE FROM spiritual_completions WHERE completionDate < :cutoffDate")
    suspend fun deleteOldCompletions(cutoffDate: String)

    @Query("""
        SELECT COUNT(*) FROM spiritual_completions
        WHERE completionDate = :date
    """)
    suspend fun getCompletionCountForDate(date: String): Int
}

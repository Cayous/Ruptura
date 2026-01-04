package com.zenapp.focus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zenapp.focus.data.local.entity.SpiritualActivityEntity

@Dao
interface SpiritualActivityDao {
    @Query("SELECT * FROM spiritual_activities ORDER BY orderIndex ASC")
    suspend fun getAllActivities(): List<SpiritualActivityEntity>

    @Query("SELECT * FROM spiritual_activities WHERE id = :activityId")
    suspend fun getActivity(activityId: String): SpiritualActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<SpiritualActivityEntity>)

    @Query("DELETE FROM spiritual_activities")
    suspend fun deleteAll()
}

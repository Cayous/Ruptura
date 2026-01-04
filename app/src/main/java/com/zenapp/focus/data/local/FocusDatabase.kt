package com.zenapp.focus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zenapp.focus.data.local.dao.SessionDao
import com.zenapp.focus.data.local.dao.SessionStatsDao
import com.zenapp.focus.data.local.dao.SpiritualActivityDao
import com.zenapp.focus.data.local.dao.SpiritualCompletionDao
import com.zenapp.focus.data.local.entity.SessionEntity
import com.zenapp.focus.data.local.entity.SessionStatsEntity
import com.zenapp.focus.data.local.entity.SpiritualActivityEntity
import com.zenapp.focus.data.local.entity.SpiritualCompletionEntity

@Database(
    entities = [
        SessionEntity::class,
        SessionStatsEntity::class,
        SpiritualActivityEntity::class,
        SpiritualCompletionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun sessionStatsDao(): SessionStatsDao
    abstract fun spiritualActivityDao(): SpiritualActivityDao
    abstract fun spiritualCompletionDao(): SpiritualCompletionDao
}

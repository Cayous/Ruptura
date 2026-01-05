package com.ruptura.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ruptura.app.data.local.dao.SessionDao
import com.ruptura.app.data.local.dao.SessionStatsDao
import com.ruptura.app.data.local.dao.SpiritualActivityDao
import com.ruptura.app.data.local.dao.SpiritualCompletionDao
import com.ruptura.app.data.local.entity.SessionEntity
import com.ruptura.app.data.local.entity.SessionStatsEntity
import com.ruptura.app.data.local.entity.SpiritualActivityEntity
import com.ruptura.app.data.local.entity.SpiritualCompletionEntity

@Database(
    entities = [
        SessionEntity::class,
        SessionStatsEntity::class,
        SpiritualActivityEntity::class,
        SpiritualCompletionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun sessionStatsDao(): SessionStatsDao
    abstract fun spiritualActivityDao(): SpiritualActivityDao
    abstract fun spiritualCompletionDao(): SpiritualCompletionDao
}

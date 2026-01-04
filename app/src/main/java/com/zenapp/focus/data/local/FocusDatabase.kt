package com.zenapp.focus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zenapp.focus.data.local.dao.SessionDao
import com.zenapp.focus.data.local.dao.SessionStatsDao
import com.zenapp.focus.data.local.entity.SessionEntity
import com.zenapp.focus.data.local.entity.SessionStatsEntity

@Database(
    entities = [SessionEntity::class, SessionStatsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun sessionStatsDao(): SessionStatsDao
}

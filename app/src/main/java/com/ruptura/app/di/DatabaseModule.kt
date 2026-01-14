package com.ruptura.app.di

import android.content.Context
import androidx.room.Room
import com.ruptura.app.data.local.FocusDatabase
import com.ruptura.app.data.local.dao.SessionDao
import com.ruptura.app.data.local.dao.SessionStatsDao
import com.ruptura.app.data.local.migrations.MIGRATION_4_5
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFocusDatabase(
        @ApplicationContext context: Context
    ): FocusDatabase {
        return Room.databaseBuilder(
            context,
            FocusDatabase::class.java,
            "focus_database"
        )
            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSessionDao(database: FocusDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    fun provideSessionStatsDao(database: FocusDatabase): SessionStatsDao {
        return database.sessionStatsDao()
    }

    @Provides
    fun provideSpiritualActivityDao(database: FocusDatabase): com.ruptura.app.data.local.dao.SpiritualActivityDao {
        return database.spiritualActivityDao()
    }

    @Provides
    fun provideSpiritualCompletionDao(database: FocusDatabase): com.ruptura.app.data.local.dao.SpiritualCompletionDao {
        return database.spiritualCompletionDao()
    }

    @Provides
    fun provideSpiritualScheduleDao(database: FocusDatabase): com.ruptura.app.data.local.dao.SpiritualScheduleDao {
        return database.spiritualScheduleDao()
    }

    @Provides
    fun provideScheduleSnoozeDao(database: FocusDatabase): com.ruptura.app.data.local.dao.ScheduleSnoozeDao {
        return database.scheduleSnoozeDao()
    }
}

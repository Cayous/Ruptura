package com.zenapp.focus.di

import android.content.Context
import androidx.room.Room
import com.zenapp.focus.data.local.FocusDatabase
import com.zenapp.focus.data.local.dao.SessionDao
import com.zenapp.focus.data.local.dao.SessionStatsDao
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
    fun provideSpiritualActivityDao(database: FocusDatabase): com.zenapp.focus.data.local.dao.SpiritualActivityDao {
        return database.spiritualActivityDao()
    }

    @Provides
    fun provideSpiritualCompletionDao(database: FocusDatabase): com.zenapp.focus.data.local.dao.SpiritualCompletionDao {
        return database.spiritualCompletionDao()
    }
}

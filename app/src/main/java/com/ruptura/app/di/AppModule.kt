package com.ruptura.app.di

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.ruptura.app.data.repository.UsageRepositoryImpl
import com.ruptura.app.domain.repository.UsageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUsageStatsManager(
        @ApplicationContext context: Context
    ): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager {
        return context.packageManager
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUsageRepository(
        impl: UsageRepositoryImpl
    ): UsageRepository

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(
        impl: com.ruptura.app.data.repository.FocusSessionRepositoryImpl
    ): com.ruptura.app.domain.repository.FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindSpiritualRepository(
        impl: com.ruptura.app.data.repository.SpiritualRepositoryImpl
    ): com.ruptura.app.domain.repository.SpiritualRepository

    @Binds
    @Singleton
    abstract fun bindSpiritualScheduleRepository(
        impl: com.ruptura.app.data.repository.SpiritualScheduleRepositoryImpl
    ): com.ruptura.app.domain.repository.SpiritualScheduleRepository
}

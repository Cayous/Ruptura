package com.zenapp.focus.di

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.zenapp.focus.data.repository.UsageRepositoryImpl
import com.zenapp.focus.domain.repository.UsageRepository
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
        impl: com.zenapp.focus.data.repository.FocusSessionRepositoryImpl
    ): com.zenapp.focus.domain.repository.FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindSpiritualRepository(
        impl: com.zenapp.focus.data.repository.SpiritualRepositoryImpl
    ): com.zenapp.focus.domain.repository.SpiritualRepository
}

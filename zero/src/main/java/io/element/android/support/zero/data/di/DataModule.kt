package io.element.android.support.zero.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.DataCleanerImpl
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.delegate.PreferencesImpl

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun providePreferences(preferences: PreferencesImpl): Preferences

    @Binds
    fun provideDataCleaner(dataCleaner: DataCleanerImpl): DataCleaner
}

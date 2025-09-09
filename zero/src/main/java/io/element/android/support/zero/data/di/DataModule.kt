package io.element.android.support.zero.data.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.DataCleanerImpl
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.delegate.PreferencesImpl
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.datastore.DatastoreCleaner

@BindingContainer
@ContributesTo(AppScope::class)
object DataModule {

    @Provides
    @SingleIn(AppScope::class)
    fun providePreferences(appPreferences: AppPreferences): Preferences =
        PreferencesImpl(appPreferences)

    @Provides
    @SingleIn(AppScope::class)
    fun provideDataCleaner(datastoreCleaner: DatastoreCleaner): DataCleaner =
        DataCleanerImpl(datastoreCleaner)
}

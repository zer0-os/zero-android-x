package io.element.android.support.zero.data.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.DataCleanerImpl
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.delegate.PreferencesImpl
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.datastore.DatastoreCleaner

@Module
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

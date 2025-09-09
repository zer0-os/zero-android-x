package io.element.android.support.zero.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.datastore.DatastoreCleaner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@BindingContainer
@ContributesTo(AppScope::class)
object DatastoreModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile("app_preferences") }
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideDatastoreCleaner(dataStore: DataStore<Preferences>): DatastoreCleaner = DatastoreCleaner(dataStore)

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppPreferences(dataStore: DataStore<Preferences>): AppPreferences = AppPreferences(dataStore)
}

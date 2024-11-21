package io.element.android.support.zero.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.element.android.libraries.di.AppScope
import io.element.android.libraries.di.ApplicationContext
import io.element.android.libraries.di.SingleIn
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.datastore.DatastoreCleaner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
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
    fun provideDatastoreCleaner(dataStore: DataStore<Preferences>) = DatastoreCleaner(dataStore)

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppPreferences(dataStore: DataStore<Preferences>) = AppPreferences(dataStore)
}

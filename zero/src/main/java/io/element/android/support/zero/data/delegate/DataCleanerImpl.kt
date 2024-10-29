package io.element.android.support.zero.data.delegate

import io.element.android.support.zero.datastore.DatastoreCleaner
import javax.inject.Inject

internal class DataCleanerImpl
@Inject
constructor(
    private val datastore: DatastoreCleaner,
) : DataCleaner {

    override suspend fun clean() {
        datastore.clean()
    }
}

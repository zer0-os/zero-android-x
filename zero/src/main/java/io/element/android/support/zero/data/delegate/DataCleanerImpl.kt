package io.element.android.support.zero.data.delegate

import io.element.android.support.zero.datastore.DatastoreCleaner

class DataCleanerImpl(
    private val datastore: DatastoreCleaner,
) : DataCleaner {

    override suspend fun clean() {
        datastore.clean()
    }
}

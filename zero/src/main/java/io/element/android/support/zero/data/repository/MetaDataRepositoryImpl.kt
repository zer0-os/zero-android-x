/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.LinkPreviewMetaDataFilter
import io.element.android.support.zero.network.model.response.ApiLinkPreview
import io.element.android.support.zero.network.service.ZeroMetaDataService

data class MetaDataRepositoryImpl(
    private val zeroMetaDataService: ZeroMetaDataService
): MetaDataRepository {

    override suspend fun fetchLinkPreview(url: String): ApiLinkPreview? {
        return runCatching {
            val filter = LinkPreviewMetaDataFilter.newFilter(url)
            zeroMetaDataService.fetchLinkPreviewMetaData(filter)
        }.getOrNull()
    }
}

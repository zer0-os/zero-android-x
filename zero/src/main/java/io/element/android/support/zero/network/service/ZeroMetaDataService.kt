/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.LinkPreviewMetaDataFilter
import io.element.android.support.zero.network.model.response.ApiLinkPreview
import retrofit2.http.GET
import retrofit2.http.Query

interface ZeroMetaDataService {

    @GET(value = "linkPreviews")
    suspend fun fetchLinkPreviewMetaData(
        @Query("filter") filter: String = LinkPreviewMetaDataFilter.emptyFilter()
    ): ApiLinkPreview
}

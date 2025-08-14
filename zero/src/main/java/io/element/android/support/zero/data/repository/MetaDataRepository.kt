/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.feed.ApiFeedMediaResponse
import io.element.android.support.zero.network.model.response.metadata.ApiLinkPreview
import io.element.android.support.zero.network.model.response.feed.ApiUploadFeedMedia
import io.element.android.support.zero.network.model.response.metadata.ApiYoutubeLinkPreview
import java.io.File

interface MetaDataRepository {
    suspend fun fetchLinkPreview(url: String): ApiLinkPreview?

    suspend fun fetchFeedMedia(mediaId: String, isPreview: Boolean = true): ApiFeedMediaResponse?

    suspend fun uploadFeedMedia(media: File, mimeType: String): ApiUploadFeedMedia?

    suspend fun fetchYoutubeLinkPreview(url: String): ApiYoutubeLinkPreview?
}

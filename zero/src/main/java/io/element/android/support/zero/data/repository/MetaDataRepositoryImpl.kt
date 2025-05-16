/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.LinkPreviewMetaDataFilter
import io.element.android.support.zero.network.model.response.ApiFeedMediaResponse
import io.element.android.support.zero.network.model.response.ApiLinkPreview
import io.element.android.support.zero.network.model.response.ApiUploadFeedMedia
import io.element.android.support.zero.network.model.response.ApiYoutubeLinkPreview
import io.element.android.support.zero.network.service.ZeroMetaDataService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class MetaDataRepositoryImpl(
    private val zeroMetaDataService: ZeroMetaDataService
): MetaDataRepository {

    override suspend fun fetchLinkPreview(url: String): ApiLinkPreview? {
        return runCatching {
            val filter = LinkPreviewMetaDataFilter.newFilter(url)
            zeroMetaDataService.fetchLinkPreviewMetaData(filter)
        }.getOrNull()
    }

    override suspend fun fetchFeedMedia(mediaId: String): ApiFeedMediaResponse? {
        return runCatching {
            zeroMetaDataService.fetchFeedMedia(mediaId)
        }.getOrNull()
    }

    override suspend fun uploadFeedMedia(media: File, mimeType: String): ApiUploadFeedMedia? {
        return runCatching {
            val requestFile = media.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", media.name, requestFile)
            zeroMetaDataService.uploadFeedMedia(body)
        }.getOrNull()
    }

    override suspend fun fetchYoutubeLinkPreview(url: String): ApiYoutubeLinkPreview? {
        return runCatching {
            zeroMetaDataService.getYoutubeLinkMetaData(youtubeUrl = url)
        }.getOrNull()
    }
}

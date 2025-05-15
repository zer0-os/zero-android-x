/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.LinkPreviewMetaDataFilter
import io.element.android.support.zero.network.model.response.ApiFeedMediaResponse
import io.element.android.support.zero.network.model.response.ApiLinkPreview
import io.element.android.support.zero.network.model.response.ApiUploadFeedMedia
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ZeroMetaDataService {

    @GET(value = "linkPreviews")
    suspend fun fetchLinkPreviewMetaData(
        @Query("filter") filter: String = LinkPreviewMetaDataFilter.emptyFilter()
    ): ApiLinkPreview

    @GET(value = "api/media/{media_id}")
    suspend fun fetchFeedMedia(
        @Path("media_id") mediaId: String
    ): ApiFeedMediaResponse

    @Multipart
    @POST(value = "api/media")
    suspend fun uploadFeedMedia(
        @Part media: MultipartBody.Part
    ): ApiUploadFeedMedia
}

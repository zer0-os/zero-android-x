/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.MeowFeedRequest
import io.element.android.support.zero.network.model.request.PostNewFeedRequest
import io.element.android.support.zero.network.model.response.ApiFeedDetails
import io.element.android.support.zero.network.model.response.ApiFeedReplies
import io.element.android.support.zero.network.model.response.ApiFeeds
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZeroFeedService {

    @GET(value = "api/v2/posts")
    suspend fun fetchAllFeeds(
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0,
        @Query("include_replies") includeReplies: String = "true",
        @Query("include_meows") includeMeows: String = "true",
    ): ApiFeeds

    @GET(value = "api/v2/posts/channel/{primary_zId}")
    suspend fun fetchMyFeeds(
        @Path("primary_zId") primaryZId: String,
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0,
        @Query("include_replies") includeReplies: String = "true",
        @Query("include_meows") includeMeows: String = "true",
    ): ApiFeeds

    @GET(value = "api/v2/posts")
    suspend fun fetchAllUserFeeds(
        @Query("user_id") userId: String,
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0,
        @Query("include_replies") includeReplies: String = "true",
        @Query("include_meows") includeMeows: String = "true",
    ): ApiFeeds

    @GET(value = "api/v2/posts/{feed_id}")
    suspend fun fetchFeedDetails(
        @Path("feed_id") feedId: String,
        @Query("include_replies") includeReplies: String = "true",
        @Query("include_meows") includeMeows: String = "true",
    ): ApiFeedDetails

    @GET(value = "api/v2/posts/{feed_id}/replies")
    suspend fun fetchFeedReplies(
        @Path("feed_id") feedId: String,
        @Query("limit") limit: Int = 10,
        @Query("skip") skip: Int = 0,
        @Query("include_replies") includeReplies: String = "true",
        @Query("include_meows") includeMeows: String = "true",
    ): ApiFeedReplies

    @POST(value = "api/v2/posts/post/{feed_id}/meow")
    suspend fun meowFeed(
        @Path("feed_id") feedId: String,
        @Body request: MeowFeedRequest
    ): Response<ResponseBody>

    @POST(value = "api/v2/posts/channel/raw/{channel_zid}")
    suspend fun postNewFeed(
        @Path("channel_zid") channelZId: String,
        @Body request: PostNewFeedRequest
    ): Response<ResponseBody>
}

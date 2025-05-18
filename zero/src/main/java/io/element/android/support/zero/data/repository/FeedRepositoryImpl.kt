/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import io.element.android.support.zero.network.model.request.MeowFeedRequest
import io.element.android.support.zero.network.model.request.PostNewFeedRequest
import io.element.android.support.zero.network.model.response.ApiFeed
import io.element.android.support.zero.network.service.ZeroFeedService

class FeedRepositoryImpl(
    private val zeroFeedService: ZeroFeedService
) : FeedRepository {

    override suspend fun fetchAllFeeds(followingFeeds: Boolean, limit: Int, skip: Int, includeReplies: Boolean, includeMeows: Boolean): List<ApiFeed> {
        return runCatching {
            zeroFeedService.fetchAllFeeds(
                following = followingFeeds.toString(),
                limit = limit,
                skip = skip,
                includeReplies = includeReplies.toString(),
                includeMeows = includeMeows.toString()
            ).feeds
        }.getOrDefault(emptyList())
    }

    override suspend fun fetchAllMyFeeds(primaryZId: String, limit: Int, skip: Int, includeReplies: Boolean, includeMeows: Boolean): List<ApiFeed> {
        return runCatching {
            zeroFeedService.fetchMyFeeds(
                primaryZId = primaryZId.replace(ZERO_CHANNEL_PREFIX, ""),
                limit = limit,
                skip = skip,
                includeReplies = includeReplies.toString(),
                includeMeows = includeMeows.toString()
            ).feeds
        }.getOrDefault(emptyList())
    }

    override suspend fun fetchFeedDetails(feedId: String, includeReplies: Boolean, includeMeows: Boolean): ApiFeed? {
        return runCatching {
            zeroFeedService.fetchFeedDetails(
                feedId = feedId,
                includeReplies = includeReplies.toString(),
                includeMeows = includeMeows.toString()
            ).feed
        }.getOrDefault(null)
    }

    override suspend fun fetchFeedReplies(feedId: String, limit: Int, skip: Int, includeReplies: Boolean, includeMeows: Boolean): List<ApiFeed> {
        return runCatching {
            zeroFeedService.fetchFeedReplies(
                feedId = feedId,
                limit = limit,
                skip = skip,
                includeReplies = includeReplies.toString(),
                includeMeows = includeMeows.toString()
            ).feedReplies
        }.getOrDefault(emptyList())
    }

    override suspend fun addMeowToFeed(feedId: String, meowAmount: Int): ApiFeed? {
        return runCatching {
            val result = zeroFeedService.meowFeed(feedId, MeowFeedRequest(amount = meowAmount))
            if (result.isSuccessful) {
                zeroFeedService.fetchFeedDetails(feedId).feed
            } else {
                null
            }
        }.getOrNull()
    }

    override suspend fun createNewFeed(channelZId: String, content: String, mediaId: String?, replyToPost: String?): Boolean {
        return runCatching {
            val result = zeroFeedService.postNewFeed(
                channelZId = channelZId.replace(ZERO_CHANNEL_PREFIX, ""),
                request = PostNewFeedRequest.newRequest(content, mediaId, replyToPost)
            )
            result.isSuccessful
        }.getOrDefault(false)
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.ApiFeed

interface FeedRepository {

    suspend fun fetchAllFeeds(
        limit: Int = 10,
        skip: Int = 0,
        includeReplies: Boolean = true,
        includeMeows: Boolean = true
    ): List<ApiFeed>

    suspend fun fetchAllMyFeeds(
        primaryZId: String,
        limit: Int = 10,
        skip: Int = 0,
        includeReplies: Boolean = true,
        includeMeows: Boolean = true
    ): List<ApiFeed>

    suspend fun fetchAllUserFeeds(
        userId: String,
        limit: Int = 10,
        skip: Int = 0,
        includeReplies: Boolean = true,
        includeMeows: Boolean = true
    ): List<ApiFeed>

    suspend fun fetchFeedDetails(
        feedId: String,
        includeReplies: Boolean = true,
        includeMeows: Boolean = true
    ): ApiFeed?

    suspend fun fetchFeedReplies(
        feedId: String,
        limit: Int = 10,
        skip: Int = 0,
        includeReplies: Boolean = true,
        includeMeows: Boolean = true
    ): List<ApiFeed>

    suspend fun addMeowToFeed(feedId: String, meowAmount: Int): ApiFeed?

    suspend fun createNewFeed(channelZId: String, content: String, mediaId: String?, replyToPost: String?): Boolean
}

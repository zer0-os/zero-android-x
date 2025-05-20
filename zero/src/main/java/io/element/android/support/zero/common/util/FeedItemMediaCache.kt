/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.util

import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview

object FeedItemMediaCache {
    private val feedItemMediaMap = mutableMapOf<String, FeedMedia>()
    private val feedItemLinkMetaDataMap = mutableMapOf<String, ZeroLinkPreview>()

    fun getCachedFeedItemMediaMap() = feedItemMediaMap.toMap()
    fun getCachedFeedItemLinkMetaDataMap() = feedItemLinkMetaDataMap.toMap()

    fun addFeedMedia(feedId: String, media: FeedMedia) {
        feedItemMediaMap[feedId] = media
    }

    fun addLinkMetaData(feedId: String, linkMetaData: ZeroLinkPreview) {
        feedItemLinkMetaDataMap[feedId] = linkMetaData
    }

    fun containsMedia(feedId: String): Boolean = feedItemMediaMap.contains(feedId)

    fun containsUrlMetaData(feedId: String): Boolean = feedItemLinkMetaDataMap.contains(feedId)
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.datasource

import io.element.android.libraries.matrix.api.zero.feed.FeedMedia

object FeedItemMediaCache {
    private val feedItemMediaMap = mutableMapOf<String, FeedMedia>()

    fun getCachedFeedItemMediaMap() = feedItemMediaMap.toMap()

    fun addFeedMedia(feedId: String, media: FeedMedia) {
        feedItemMediaMap[feedId] = media
    }
}

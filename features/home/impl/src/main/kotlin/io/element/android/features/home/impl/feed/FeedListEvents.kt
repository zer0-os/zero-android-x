/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

sealed interface FeedListEvents {
    data class LoadMoreFeeds(val currentFeeds: List<ZeroFeed>, val followingFeeds: Boolean) : FeedListEvents
    data class RefreshFeeds(val followingFeeds: Boolean) : FeedListEvents
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int) : FeedListEvents
    data class LoadFeedMedia(val mediaId: String) : FeedListEvents
    data object DismissFeedMedia : FeedListEvents
}

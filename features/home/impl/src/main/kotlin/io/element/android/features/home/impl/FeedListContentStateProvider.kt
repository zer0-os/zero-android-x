/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import kotlinx.collections.immutable.toPersistentList

open class FeedListContentStateProvider : PreviewParameterProvider<FeedListContentState> {
    override val values: Sequence<FeedListContentState>
        get() = sequenceOf(
            aFeedListContentState(placeholderFeeds()),
            aPlaceholderFeedListContentState(),
            aSkeletonFeedListContentState(),
            anEmptyFeedListContentState()
        )
}

private fun placeholderFeeds(): List<ZeroFeed> {
    val list = mutableListOf<ZeroFeed>()
    for (i in 0..5) {
        list.add(ZeroFeed.placeholder)
    }
    return list
}

internal fun aPlaceholderFeedListContentState() =
    FeedListContentState.Feeds(placeholderFeeds().toPersistentList())

internal fun aFeedListContentState(
    feeds: List<ZeroFeed>
) = FeedListContentState.Feeds(feeds.toPersistentList())

internal fun aSkeletonFeedListContentState() = FeedListContentState.Skeleton(20)

internal fun anEmptyFeedListContentState() = FeedListContentState.Empty

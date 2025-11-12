/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import kotlinx.collections.immutable.toPersistentList

open class FeedListStateProvider : PreviewParameterProvider<FeedListState> {
    override val values: Sequence<FeedListState>
        get() = sequenceOf(
            aFeedListState(),
            aFeedListState(contentState = aFeedListContentState(placeholderFeeds())),
            aFeedListState(contentState = aSkeletonFeedListContentState()),
            aFeedListState(contentState = anEmptyFeedListContentState())
        )
}

internal fun aFeedListState(
    feedMediaMap: Map<String, FeedMedia> = emptyMap(),
    feedLinkMetaDataMap: Map<String, ZeroLinkPreview> = emptyMap(),
    contentState: FeedListContentState = aPlaceholderFeedListContentState()
) = FeedListState(
    genericActionState = AsyncAction.Uninitialized,
    contentState = contentState,
    feedMediaMap = feedMediaMap,
    feedLinkMetaDataMap = feedLinkMetaDataMap,
    feedMediaPreviewState = AsyncAction.Uninitialized,
    eventSink = {}
)

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

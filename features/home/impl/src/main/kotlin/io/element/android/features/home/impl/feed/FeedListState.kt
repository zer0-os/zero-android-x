/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import kotlinx.collections.immutable.ImmutableList

data class FeedListState(
    val genericActionState: AsyncAction<Unit>,

    val contentState: FeedListContentState,
    val feedMediaMap: Map<String, FeedMedia>,
    val feedLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    val feedMediaPreviewState: AsyncAction<FeedMedia> = AsyncAction.Uninitialized,

    val eventSink: (FeedListEvents) -> Unit
)

@Immutable
sealed interface FeedListContentState {
    data class Skeleton(val count: Int) : FeedListContentState
    data object Empty : FeedListContentState
    data class Feeds(
        val feeds: ImmutableList<ZeroFeed>
    ) : FeedListContentState
}

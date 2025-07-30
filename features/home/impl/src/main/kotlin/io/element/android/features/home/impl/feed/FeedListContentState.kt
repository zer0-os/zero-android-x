/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface FeedListContentState {
    data class Skeleton(val count: Int) : FeedListContentState
    data object Empty : FeedListContentState
    data class Feeds(
        val feeds: ImmutableList<ZeroFeed>
    ) : FeedListContentState
}

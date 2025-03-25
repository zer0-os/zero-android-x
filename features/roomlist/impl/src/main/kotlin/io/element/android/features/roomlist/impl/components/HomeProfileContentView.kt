/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.roomlist.impl.FeedListContentState
import io.element.android.features.roomlist.impl.FeedListContentStateProvider
import io.element.android.features.roomlist.impl.RoomListEvents
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@Composable
fun HomeProfileContentView(
    contentState: FeedListContentState,
    eventSink: (RoomListEvents) -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    modifier: Modifier = Modifier,
) {

}

@PreviewsDayNight
@Composable
internal fun HomeProfileContentViewPreview(@PreviewParameter(FeedListContentStateProvider::class) state: FeedListContentState) = ElementPreview {
    HomeProfileContentView(
        contentState = state,
        eventSink = {},
        onFeedClick = {}
    )
}

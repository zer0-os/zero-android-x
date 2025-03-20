/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.roomlist.impl.ChannelListContentState
import io.element.android.features.roomlist.impl.ChannelListContentStateProvider
import io.element.android.features.roomlist.impl.RoomListEvents
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider

@Composable
fun HomeChannelListContentView(
    contentState: ChannelListContentState,
    eventSink: (RoomListEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (contentState) {
            is ChannelListContentState.Skeleton -> {
                SkeletonView(
                    count = contentState.count,
                )
            }
            is ChannelListContentState.Empty -> {
                EmptyView(modifier = modifier)
            }
            is ChannelListContentState.Channels -> {
                ChannelsViewList(
                    state = contentState,
                    eventSink = eventSink
                )
            }
        }
    }
}

@Composable
private fun SkeletonView(count: Int, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(count) { index ->
            item {
                HomeChannelPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyView(
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = "No Channels yet",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ChannelsViewList(
    state: ChannelListContentState.Channels,
    eventSink: (RoomListEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(
            items = state.channels,
        ) { index, channel ->
            HomeChannelRow(
                channel = channel,
                onChannelClick = {
                    eventSink(RoomListEvents.OpenChannel(channel))
                }
            )
            if (index != state.channels.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun HomeChannelListContentViewPreview(@PreviewParameter(ChannelListContentStateProvider::class) state: ChannelListContentState) = ElementPreview {
    HomeChannelListContentView(
        contentState = state,
        eventSink = {}
    )
}

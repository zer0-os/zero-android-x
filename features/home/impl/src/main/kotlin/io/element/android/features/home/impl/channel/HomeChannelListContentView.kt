/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider

@Composable
fun HomeChannelListContentView(
    contentState: ChannelListContentState,
    eventSink: (HomeEvents) -> Unit,
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
                HomeTabContentEmptyView(modifier = modifier, text = "No channels yet")
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
private fun ChannelsViewList(
    state: ChannelListContentState.Channels,
    eventSink: (HomeEvents) -> Unit,
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
                    eventSink(HomeEvents.OpenChannel(channel))
                }
            )
            if (index != state.channels.lastIndex) {
                HorizontalDivider()
            }
        }
        item {
            Spacer(Modifier.size(100.dp))
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

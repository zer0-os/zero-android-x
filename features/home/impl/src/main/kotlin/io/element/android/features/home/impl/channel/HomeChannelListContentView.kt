/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.aHomeState
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.model.ChannelsScreenTab
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.roomlist.RoomListContentState
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.RoomListSkeletonView
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.roomlist.RoomSummaryRow
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider

@Composable
fun HomeChannelListContentView(
    channelsContentState: ChannelListContentState,
    roomListState: RoomListState,
    eventSink: (HomeEvents) -> Unit,
    roomEventSink: (RoomListEvents) -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedChannelsTab = rememberSaveable { mutableStateOf(ChannelsScreenTab.ALL) }
    Column(modifier = modifier) {
        ChannelsScreenTabView(
            onTabSelected = { tab ->
                selectedChannelsTab.value = tab
            }
        )
        when (selectedChannelsTab.value) {
            ChannelsScreenTab.ALL,
            ChannelsScreenTab.MUTED -> {
                Box {
                    when (roomListState.contentState) {
                        is RoomListContentState.Skeleton -> {
                            RoomListSkeletonView(
                                modifier = modifier,
                                count = roomListState.contentState.count,
                                contentPadding = PaddingValues(0.dp),
                            )
                        }
                        is RoomListContentState.Empty -> {
                            HomeTabContentEmptyView(modifier = modifier, text = "No chats")
                        }
                        is RoomListContentState.Rooms -> {
                            ChannelTabRoomsViewList(
                                state = roomListState.contentState,
                                roomMappedUserProStatus = roomListState.roomMappedUserProStatus,
                                hideInvitesAvatars = roomListState.hideInvitesAvatars,
                                selectedTab = selectedChannelsTab.value,
                                eventSink = roomEventSink,
                                onRoomClick = onRoomClick
                            )
                        }
                    }
                }
            }
            ChannelsScreenTab.Gated -> {
                Box {
                    when (channelsContentState) {
                        is ChannelListContentState.Skeleton -> {
                            SkeletonView(
                                count = channelsContentState.count,
                            )
                        }
                        is ChannelListContentState.Empty -> {
                            HomeTabContentEmptyView(modifier = modifier, text = "No channels yet")
                        }
                        is ChannelListContentState.Channels -> {
                            ChannelsViewList(
                                state = channelsContentState,
                                eventSink = eventSink
                            )
                        }
                    }
                }
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

@Composable
private fun ChannelTabRoomsViewList(
    state: RoomListContentState.Rooms,
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    selectedTab: ChannelsScreenTab,
    eventSink: (RoomListEvents) -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val visibleRange by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val firstItemIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            val size = layoutInfo.visibleItemsInfo.size
            firstItemIndex until firstItemIndex + size
        }
    }
    val updatedEventSink by rememberUpdatedState(newValue = eventSink)
    LaunchedEffect(visibleRange) {
        updatedEventSink(RoomListEvents.UpdateVisibleRange(visibleRange))
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
    ) {
        val roomsList = state.summaries.filter { !it.isAChannel }
        val filteredRoomsList = when (selectedTab) {
            ChannelsScreenTab.ALL ->
                roomsList.filter { !it.isEncrypted }
            ChannelsScreenTab.MUTED -> {
                roomsList.filter { it.isMuted }
            }
            else -> roomsList
        }
        // Note: do not use a key for the LazyColumn, or the scroll will not behave as expected if a room
        // is moved to the top of the list.
        itemsIndexed(
            items = filteredRoomsList,
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            RoomSummaryRow(
                room = room,
                showProBadgeWithRoom = roomMappedUserProStatus.getOrDefault(room.id, false),
                hideInviteAvatars = hideInvitesAvatars,
                isInviteSeen = room.displayType == RoomSummaryDisplayType.INVITE &&
                    state.seenRoomInvites.contains(room.roomId),
                onClick = onRoomClick,
                eventSink = eventSink,
            )
            if (index != state.summaries.lastIndex) {
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
internal fun HomeChannelListContentViewPreview() = ElementPreview {
    HomeChannelListContentView(
        channelsContentState = aHomeState().channelContentState,
        roomListState = aHomeState().roomListState,
        onRoomClick = {},
        eventSink = {},
        roomEventSink = {}
    )
}

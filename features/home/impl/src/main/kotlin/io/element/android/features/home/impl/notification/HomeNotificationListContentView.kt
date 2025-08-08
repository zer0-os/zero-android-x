/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.notification

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.model.NotificationsScreenTab
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.roomlist.RoomListContentState
import io.element.android.features.home.impl.roomlist.RoomListContentStateProvider
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun HomeNotificationListContentView(
    contentState: RoomListContentState,
    eventSink: (RoomListEvents) -> Unit,
    onNotificationClick: (RoomListRoomSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedNotificationTab = rememberSaveable { mutableStateOf(NotificationsScreenTab.ALL) }
    Column(modifier = modifier) {
        NotificationsScreenTabView(
            onTabSelected = { tab ->
                selectedNotificationTab.value = tab
            }
        )
        Box {
            when (contentState) {
                is RoomListContentState.Skeleton -> {
                    SkeletonView(
                        count = contentState.count,
                    )
                }
                is RoomListContentState.Empty -> {
                    HomeTabContentEmptyView(modifier = modifier, text = "No new notifications")
                }
                is RoomListContentState.Rooms -> {
                    val items = contentState.summaries
                        .filter { it.hasNewContent && it.displayType !in (RoomSummaryDisplayType.KNOCKED..RoomSummaryDisplayType.PLACEHOLDER) }
                        .let { roomListRoomSummaries ->
                            when (selectedNotificationTab.value) {
                                NotificationsScreenTab.ALL -> roomListRoomSummaries
                                NotificationsScreenTab.HIGHLIGHTS -> roomListRoomSummaries.filter {
                                    it.numberOfUnreadMentions > 0
                                }
                                NotificationsScreenTab.MUTED -> roomListRoomSummaries.filter {
                                    it.userDefinedNotificationMode == RoomNotificationMode.MUTE
                                }
                            }
                        }
                        .toPersistentList()
                    if (items.isEmpty()) {
                        HomeTabContentEmptyView(modifier = modifier, text = "No new notifications")
                    } else {
                        NotificationsViewList(
                            items = items,
                            eventSink = eventSink,
                            onNotificationClick = onNotificationClick
                        )
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
                HomeNotificationPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun NotificationsViewList(
    items: ImmutableList<RoomListRoomSummary>,
    eventSink: (RoomListEvents) -> Unit,
    onNotificationClick: (RoomListRoomSummary) -> Unit,
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
        // FAB height is 56dp, bottom padding is 16dp, we add 8dp as extra margin -> 56+16+8 = 80
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Note: do not use a key for the LazyColumn, or the scroll will not behave as expected if a room
        // is moved to the top of the list.
        itemsIndexed(
            items = items,
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            HomeNotificationRow(
                room = room,
                onClick = onNotificationClick
            )
            /*if (index != items.lastIndex) {
                HorizontalDivider()
            }*/
        }
        item {
            Spacer(Modifier.size(100.dp))
        }
    }
}

@PreviewsDayNight
@Composable
internal fun HomeNotificationListContentViewPreview(@PreviewParameter(RoomListContentStateProvider::class) state: RoomListContentState) = ElementPreview {
    HomeNotificationListContentView(
        contentState = state,
        eventSink = {},
        onNotificationClick = {},
    )
}

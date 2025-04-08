/*
 * Copyright 2024 New Vector Ltd.
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.roomlist.impl.RoomListContentState
import io.element.android.features.roomlist.impl.RoomListContentStateProvider
import io.element.android.features.roomlist.impl.RoomListEvents
import io.element.android.features.roomlist.impl.contentType
import io.element.android.features.roomlist.impl.model.RoomListRoomSummary
import io.element.android.features.roomlist.impl.model.RoomSummaryDisplayType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun HomeNotificationListContentView(
    contentState: RoomListContentState,
    eventSink: (RoomListEvents) -> Unit,
    onNotificationClick: (RoomListRoomSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (contentState) {
            is RoomListContentState.Skeleton -> {
                SkeletonView(
                    count = contentState.count,
                )
            }
            is RoomListContentState.Empty -> {
                EmptyView(modifier = modifier)
            }
            is RoomListContentState.Rooms -> {
                val items = contentState.summaries
                    .filter { it.hasNewContent && it.displayType !in (RoomSummaryDisplayType.KNOCKED..RoomSummaryDisplayType.PLACEHOLDER) }
                    .toPersistentList()
                if (items.isEmpty()) {
                    EmptyView(modifier = modifier)
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
private fun EmptyView(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = "No new notifications",
            modifier = Modifier.align(Alignment.Center),
        )
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

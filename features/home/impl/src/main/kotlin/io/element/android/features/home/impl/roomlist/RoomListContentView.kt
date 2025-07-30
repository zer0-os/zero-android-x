/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.features.home.impl.components.ConfirmRecoveryKeyBanner
import io.element.android.features.home.impl.components.EmptyScaffold
import io.element.android.features.home.impl.components.SetUpRecoveryKeyBanner
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.filters.RoomListFilter
import io.element.android.features.home.impl.filters.RoomListFiltersEmptyStateResources
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.aRoomListFiltersState
import io.element.android.features.home.impl.filters.selection.FilterSelectionState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

@Composable
fun RoomListContentView(
    modifier: Modifier = Modifier,
    contentState: RoomListContentState,
    filtersState: RoomListFiltersState,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    when (contentState) {
        is RoomListContentState.Skeleton -> {
            SkeletonView(
                modifier = modifier,
                count = contentState.count,
                contentPadding = contentPadding,
            )
        }
        is RoomListContentState.Empty -> {
            EmptyView(
                modifier = modifier.padding(contentPadding),
                state = contentState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onCreateRoomClick = onCreateRoomClick,
            )
        }
        is RoomListContentState.Rooms -> {
            RoomsView(
                modifier = modifier,
                state = contentState,
                hideInvitesAvatars = hideInvitesAvatars,
                filtersState = filtersState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = onRoomClick,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun SkeletonView(
    count: Int,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        repeat(count) { index ->
            item {
                RoomSummaryPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyView(
    state: RoomListContentState.Empty,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = R.string.screen_roomlist_empty_title,
            subtitle = R.string.screen_roomlist_empty_message,
            action = {
                Button(
                    text = stringResource(CommonStrings.action_start_chat),
                    leadingIcon = IconSource.Vector(CompoundIcons.Compose()),
                    onClick = onCreateRoomClick,
                )
            },
            modifier = Modifier.align(Alignment.Center),
        )
        Box {
            when (state.securityBannerState) {
                SecurityBannerState.SetUpRecovery -> {
                    SetUpRecoveryKeyBanner(
                        onContinueClick = onSetUpRecoveryClick,
                        onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
                    )
                }
                SecurityBannerState.RecoveryKeyConfirmation -> {
                    ConfirmRecoveryKeyBanner(
                        onContinueClick = onConfirmRecoveryKeyClick,
                        onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
                    )
                }
                SecurityBannerState.None -> Unit
            }
        }
    }
}

@Composable
private fun RoomsView(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    filtersState: RoomListFiltersState,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    if (state.summaries.isEmpty() && filtersState.hasAnyFilterSelected) {
        EmptyViewForFilterStates(
            selectedFilters = filtersState.selectedFilters(),
            modifier = modifier.fillMaxSize()
        )
    } else {
        RoomsViewList(
            state = state,
            hideInvitesAvatars = hideInvitesAvatars,
            eventSink = eventSink,
            onSetUpRecoveryClick = onSetUpRecoveryClick,
            onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            onRoomClick = onRoomClick,
            contentPadding = contentPadding,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun RoomsViewList(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
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
        contentPadding = contentPadding,
    ) {
        when (state.securityBannerState) {
            SecurityBannerState.SetUpRecovery -> {
                item {
                    SetUpRecoveryKeyBanner(
                        onContinueClick = onSetUpRecoveryClick,
                        onDismissClick = { updatedEventSink(RoomListEvents.DismissBanner) },
                    )
                }
            }
            else -> {}
            /*SecurityBannerState.RecoveryKeyConfirmation -> {
                item {
                    ConfirmRecoveryKeyBanner(
                        onContinueClick = onConfirmRecoveryKeyClick,
                        onDismissClick = { updatedEventSink(RoomListEvents.DismissBanner) },
                    )
                }
            }
            SecurityBannerState.None -> if (state.fullScreenIntentPermissionsState.shouldDisplayBanner) {
                item {
                    FullScreenIntentPermissionBanner(state = state.fullScreenIntentPermissionsState)
                }
            } else if (state.batteryOptimizationState.shouldDisplayBanner) {
                item {
                    BatteryOptimizationBanner(state = state.batteryOptimizationState)
                }
            }*/
        }

        // Note: do not use a key for the LazyColumn, or the scroll will not behave as expected if a room
        // is moved to the top of the list.
        itemsIndexed(
            items = state.summaries.filter { !it.isAChannel },
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            RoomSummaryRow(
                room = room,
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
    }
}

@Composable
private fun EmptyViewForFilterStates(
    selectedFilters: ImmutableList<RoomListFilter>,
    modifier: Modifier = Modifier,
) {
    val emptyStateResources = RoomListFiltersEmptyStateResources.fromSelectedFilters(selectedFilters) ?: return
    EmptyScaffold(
        title = emptyStateResources.title,
        subtitle = emptyStateResources.subtitle,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun RoomListContentViewPreview(@PreviewParameter(RoomListContentStateProvider::class) state: RoomListContentState) = ElementPreview {
    RoomListContentView(
        contentState = state,
        filtersState = aRoomListFiltersState(
            filterSelectionStates = RoomListFilter.entries.map {
                FilterSelectionState(
                    filter = it,
                    isSelected = true
                )
            }
        ),
        hideInvitesAvatars = false,
        eventSink = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onRoomClick = {},
        onCreateRoomClick = {},
        contentPadding = PaddingValues(0.dp),
    )
}

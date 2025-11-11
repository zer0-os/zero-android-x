/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.features.home.impl.components.EmptyScaffold
import io.element.android.features.home.impl.components.SetUpRecoveryKeyBanner
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.filters.RoomListFilter
import io.element.android.features.home.impl.filters.RoomListFiltersEmptyStateMessages
import io.element.android.features.home.impl.filters.RoomListFiltersEvents
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.aRoomListFiltersState
import io.element.android.features.home.impl.filters.selection.FilterSelectionState
import io.element.android.features.home.impl.model.ChatScreenTab
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
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    var hasShownVerifyScreen by rememberSaveable { mutableStateOf(false) }
    val showVerifyEncryptionScreen: () -> Unit = {
        if (!hasShownVerifyScreen) {
            hasShownVerifyScreen = true
            Handler(Looper.getMainLooper()).postDelayed({
                onSetUpRecoveryClick()
            }, 2_000)
        }
    }

    when (contentState) {
        is RoomListContentState.Skeleton -> {
            RoomListSkeletonView(
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
                verifyEncryption = showVerifyEncryptionScreen
            )
        }
        is RoomListContentState.Rooms -> {
            Column(modifier = modifier) {
                val selectedChatTab = rememberSaveable { mutableStateOf(ChatScreenTab.ALL) }
                RoomListScreenTabView(
                    selectedTab = selectedChatTab.value,
                    onTabSelected = { tab ->
                        selectedChatTab.value = tab
                        when (tab) {
                            ChatScreenTab.ALL -> filtersState.clearFilters()
                            ChatScreenTab.UNREAD -> filtersState.eventSink(RoomListFiltersEvents.ToggleFilter(RoomListFilter.Rooms))
                            ChatScreenTab.FAVORITE -> filtersState.eventSink(RoomListFiltersEvents.ToggleFilter(RoomListFilter.Favourites))
                        }
                    }
                )
                Box {
                    RoomsView(
                        state = contentState,
                        roomMappedUserProStatus = roomMappedUserProStatus,
                        hideInvitesAvatars = hideInvitesAvatars,
                        filtersState = filtersState,
                        eventSink = eventSink,
                        onSetUpRecoveryClick = onSetUpRecoveryClick,
                        onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                        onRoomClick = onRoomClick,
                        verifyEncryption = showVerifyEncryptionScreen,
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun RoomListSkeletonView(
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
    verifyEncryption: () -> Unit,
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
//                    ConfirmRecoveryKeyBanner(
//                        onContinueClick = onConfirmRecoveryKeyClick,
//                        onDismissClick = { eventSink(RoomListEvents.DismissBanner) },
//                    )
                    verifyEncryption.invoke()
                }
                SecurityBannerState.None -> Unit
            }
        }
    }
}

@Composable
private fun RoomsView(
    state: RoomListContentState.Rooms,
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    filtersState: RoomListFiltersState,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    verifyEncryption: () -> Unit,
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
            roomMappedUserProStatus = roomMappedUserProStatus,
            hideInvitesAvatars = hideInvitesAvatars,
            eventSink = eventSink,
            onSetUpRecoveryClick = onSetUpRecoveryClick,
            onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            onRoomClick = onRoomClick,
            verifyEncryption = verifyEncryption,
            contentPadding = contentPadding,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun RoomsViewList(
    state: RoomListContentState.Rooms,
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvents) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    verifyEncryption: () -> Unit,
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
            SecurityBannerState.RecoveryKeyConfirmation -> {
                verifyEncryption()
            }
            /*SecurityBannerState.None -> if (state.fullScreenIntentPermissionsState.shouldDisplayBanner) {
                item {
                    FullScreenIntentPermissionBanner(state = state.fullScreenIntentPermissionsState)
                }
            } else if (state.batteryOptimizationState.shouldDisplayBanner) {
                item {
                    BatteryOptimizationBanner(state = state.batteryOptimizationState)
                }
            } else if (state.showNewNotificationSoundBanner) {
                item {
                    NewNotificationSoundBanner(
                        onDismissClick = { updatedEventSink(RoomListEvents.DismissNewNotificationSoundBanner) },
                    )
                }
            }
            }*/
            else -> {}
        }

        // Note: do not use a key for the LazyColumn, or the scroll will not behave as expected if a room
        // is moved to the top of the list.
        itemsIndexed(
            items = state.summaries.filter { it.isPrimary },
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

@Composable
private fun EmptyViewForFilterStates(
    selectedFilters: ImmutableList<RoomListFilter>,
    modifier: Modifier = Modifier,
) {
    val emptyStateResources = RoomListFiltersEmptyStateMessages.fromSelectedFilters(selectedFilters) ?: return
    EmptyScaffold(
        title = emptyStateResources.title,
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
        roomMappedUserProStatus = emptyMap(),
        hideInvitesAvatars = false,
        eventSink = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onRoomClick = {},
        onCreateRoomClick = {},
        contentPadding = PaddingValues(0.dp),
    )
}

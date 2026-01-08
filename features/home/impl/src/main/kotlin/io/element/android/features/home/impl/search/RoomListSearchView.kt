/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.channel.ChannelListContentState
import io.element.android.features.home.impl.channel.aChannelListState
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.model.ChannelsScreenTab
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.toRoomSummary
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.RoomSummaryRow
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.features.roomdirectory.impl.root.RoomDirectoryEvents
import io.element.android.features.roomdirectory.impl.root.RoomDirectoryState
import io.element.android.features.roomdirectory.impl.root.aRoomDirectoryState
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.FilledTextField
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
internal fun RoomListSearchView(
    state: RoomListSearchState,
    channelsListState: ChannelListContentState,
    roomDirectoryState: RoomDirectoryState,
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    selectedHomeNavigationTab: HomeScreenTab,
    selectedChannelContentTab: ChannelsScreenTab,
    eventSink: (RoomListEvents) -> Unit,
    onRoomClick: (RoomId) -> Unit,
    onPublicRoomClick: (RoomDescription) -> Unit,
    onChannelClick: (HomeScreenChannel) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state.isSearchActive) {
        state.eventSink(RoomListSearchEvents.ToggleSearchVisibility)
    }

    AnimatedVisibility(
        visible = state.isSearchActive,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = modifier) {
            RoomListSearchContent(
                state = state,
                channelsListState = channelsListState,
                roomDirectoryState = roomDirectoryState,
                roomMappedUserProStatus = roomMappedUserProStatus,
                hideInvitesAvatars = hideInvitesAvatars,
                selectedHomeNavigationTab = selectedHomeNavigationTab,
                selectedChannelContentTab = selectedChannelContentTab,
                onRoomClick = onRoomClick,
                onPublicRoomClick = onPublicRoomClick,
                onChannelClick = onChannelClick,
                eventSink = eventSink,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomListSearchContent(
    state: RoomListSearchState,
    channelsListState: ChannelListContentState,
    roomDirectoryState: RoomDirectoryState,
    roomMappedUserProStatus: Map<String, Boolean>,
    hideInvitesAvatars: Boolean,
    selectedHomeNavigationTab: HomeScreenTab,
    selectedChannelContentTab: ChannelsScreenTab,
    eventSink: (RoomListEvents) -> Unit,
    onRoomClick: (RoomId) -> Unit,
    onPublicRoomClick: (RoomDescription) -> Unit,
    onChannelClick: (HomeScreenChannel) -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.tertiary
    val strokeWidth = 1.dp
    fun onBackButtonClick() {
        state.eventSink(RoomListSearchEvents.ToggleSearchVisibility)
    }

    fun onRoomClick(room: RoomListRoomSummary) {
        when {
            room.isDiscoverable -> {
                roomDirectoryState.roomDescriptions.firstOrNull { it.roomId == room.roomId }
                    ?.let(onPublicRoomClick)
            }
            room.isAChannel -> {
                val channelsList = if (state.query.text.isNotBlank()) {
                    ((channelsListState as? ChannelListContentState.Channels)
                        ?.channels ?: emptyList())
                } else {
                    emptyList()
                }
                channelsList.firstOrNull { it.channelFullName == room.id }
                    ?.let(onChannelClick)
            }
            else -> onRoomClick(room.roomId)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth.value
                    )
                },
                navigationIcon = { BackButton(onClick = ::onBackButtonClick) },
                title = {
                    // The stateSaver will keep the selection state when returning to this UI
                    val focusRequester = remember { FocusRequester() }
                    FilledTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        state = state.query,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        trailingIcon = if (state.query.text.isNotEmpty()) {
                            @Composable {
                                IconButton(onClick = { state.eventSink(RoomListSearchEvents.ClearQuery) }) {
                                    Icon(
                                        imageVector = CompoundIcons.Close(),
                                        contentDescription = stringResource(CommonStrings.action_cancel)
                                    )
                                }
                            }
                        } else {
                            null
                        },
                    )

                    LaunchedEffect(Unit) {
                        if (!focusRequester.restoreFocusedChild()) {
                            focusRequester.requestFocus()
                        }
                        focusRequester.saveFocusedChild()
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                val channelsList = if (state.query.text.isNotBlank()) {
                    ((channelsListState as? ChannelListContentState.Channels)
                        ?.channels ?: emptyList())
                } else {
                    emptyList()
                }
                    .filter { it.displayTitle.contains(state.query.text, true) }
                    .map { it.toRoomSummary()  }
                val publicRoomsList = if (state.query.text.isNotBlank()) {
                    roomDirectoryState.roomDescriptions
                } else {
                    emptyList()
                }
                    .map { it.toRoomSummary() }
                val roomResults = state.results
                    .filter { !it.isAChannel }
                    .toMutableList()
                    .apply {
                        // add channel search results as well
                        addAll(channelsList)
                        // add public rooms search results as well
                        addAll(publicRoomsList)
                    }
                    .distinctBy { it.roomId }
                items(
                    items = roomResults,
                    contentType = { room -> room.contentType() },
                ) { room ->
                    RoomSummaryRow(
                        room = room,
                        showProBadgeWithRoom = roomMappedUserProStatus.getOrDefault(room.id, false),
                        hideInviteAvatars = hideInvitesAvatars,
                        // TODO
                        isInviteSeen = false,
                        onClick = ::onRoomClick,
                        eventSink = eventSink,
                    )
                }
                /*if (selectedHomeNavigationTab == HomeScreenTab.CHANNEL &&
                    selectedChannelContentTab == ChannelsScreenTab.GATED) {
                    val channelsList = if (state.query.isNotBlank()) {
                        ((channelsListState as? ChannelListContentState.Channels)
                            ?.channels ?: emptyList())
                    } else {
                        emptyList()
                    }
                    val channelResults = channelsList.filter { it.channelFullName.contains(state.query, true) }

                    itemsIndexed(
                        items = channelResults,
                    ) { index, channel ->
                        HomeChannelRow(
                            channel = channel,
                            onChannelClick = { onChannelClick(channel) }
                        )
                    }

                } else {
                    val roomResults = if (selectedHomeNavigationTab == HomeScreenTab.CHAT) {
                        state.results.filter { it.isPrimary }
                    } else {
                        when (selectedChannelContentTab) {
                            ChannelsScreenTab.CHANNELS -> state.results.filter { it.isSecondary }
                            ChannelsScreenTab.MUTED -> state.results.filter { it.isMuted }
                            else -> state.results.filter { it.isPrimary }
                        }
                    }

                    items(
                        items = roomResults,
                        contentType = { room -> room.contentType() },
                    ) { room ->
                        RoomSummaryRow(
                            room = room,
                            showProBadgeWithRoom = roomMappedUserProStatus.getOrDefault(room.id, false),
                            hideInviteAvatars = hideInvitesAvatars,
                            // TODO
                            isInviteSeen = false,
                            onClick = ::onRoomClick,
                            eventSink = eventSink,
                        )
                    }
                }*/
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun RoomListSearchContentPreview(@PreviewParameter(RoomListSearchStateProvider::class) state: RoomListSearchState) = ElementPreview {
    RoomListSearchContent(
        state = state,
        channelsListState = aChannelListState().contentState,
        roomDirectoryState = aRoomDirectoryState(),
        roomMappedUserProStatus = emptyMap(),
        hideInvitesAvatars = false,
        selectedHomeNavigationTab = HomeScreenTab.CHAT,
        selectedChannelContentTab = ChannelsScreenTab.CHANNELS,
        onRoomClick = {},
        onPublicRoomClick = {},
        onChannelClick = {},
        eventSink = {},
    )
}

/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl

import io.element.android.features.roomlist.impl.model.HomeScreenChannel
import io.element.android.features.roomlist.impl.model.RoomListRoomSummary
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

sealed interface RoomListEvents {
    data class UpdateVisibleRange(val range: IntRange) : RoomListEvents
    data object DismissRequestVerificationPrompt : RoomListEvents
    data object DismissBanner : RoomListEvents
    data object ToggleSearchResults : RoomListEvents
    data class ShowContextMenu(val roomSummary: RoomListRoomSummary) : RoomListEvents

    data class AcceptInvite(val roomSummary: RoomListRoomSummary) : RoomListEvents
    data class DeclineInvite(val roomSummary: RoomListRoomSummary, val blockUser: Boolean) : RoomListEvents
    data class ShowDeclineInviteMenu(val roomSummary: RoomListRoomSummary) : RoomListEvents
    data object HideDeclineInviteMenu : RoomListEvents

    data class DismissRewardsIntimation(val immediate: Boolean = true) : RoomListEvents
    data object HideError : RoomListEvents

    sealed interface ContextMenuEvents : RoomListEvents
    data object HideContextMenu : ContextMenuEvents
    data class LeaveRoom(val roomId: RoomId) : ContextMenuEvents
    data class MarkAsRead(val roomId: RoomId) : ContextMenuEvents
    data class MarkAsUnread(val roomId: RoomId) : ContextMenuEvents
    data class SetRoomIsFavorite(val roomId: RoomId, val isFavorite: Boolean) : ContextMenuEvents
    data class ClearCacheOfRoom(val roomId: RoomId) : ContextMenuEvents

    sealed interface ChannelEvents : RoomListEvents
    data class OpenChannel(val channel: HomeScreenChannel) : ChannelEvents

    sealed interface HomeFeedEvents: RoomListEvents
    data class LoadMoreFeeds(val currentFeeds: List<ZeroFeed>, val followingFeeds: Boolean): HomeFeedEvents
    data class RefreshFeeds(val followingFeeds: Boolean): HomeFeedEvents
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int): HomeFeedEvents

    sealed interface HomeProfileEvents: RoomListEvents
    data class LoadMoreMyFeeds(val currentFeeds: List<ZeroFeed>): HomeProfileEvents
    data object RefreshMyFeeds: HomeFeedEvents
}

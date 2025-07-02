/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.roomlist.impl.filters.RoomListFiltersState
import io.element.android.features.roomlist.impl.model.HomeScreenTab
import io.element.android.features.roomlist.impl.model.RoomListRoomSummary
import io.element.android.features.roomlist.impl.search.RoomListSearchState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Immutable
data class RoomListState(
    val genericActionState: AsyncAction<Unit>,
    val contextMenu: ContextMenu,
    val declineInviteMenu: DeclineInviteMenu,
    val leaveRoomState: LeaveRoomState,
    val filtersState: RoomListFiltersState,
    val searchState: RoomListSearchState,
    val contentState: RoomListContentState,
    val channelContentState: ChannelListContentState,
    val allFeedsContentState: FeedListContentState,
    val myFeedsContentState: FeedListContentState,
    val feedMediaMap: Map<String, FeedMedia>,
    val feedLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    val resolvedChannelRoom: RoomId?,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val hideInvitesAvatars: Boolean,
    val canReportRoom: Boolean,
    val eventSink: (RoomListEvents) -> Unit,

    val shouldShowNewRewardsIntimation: Boolean = true,
    val userRewards: ZeroUserRewards = ZeroUserRewards.empty()
) {
    val displayFilters = contentState is RoomListContentState.Rooms
    private val displayActions = true

    sealed interface ContextMenu {
        data object Hidden : ContextMenu
        data class Shown(
            val roomId: RoomId,
            val roomName: String?,
            val isDm: Boolean,
            val isFavorite: Boolean,
            val markAsUnreadFeatureFlagEnabled: Boolean,
            val hasNewContent: Boolean,
            val displayClearRoomCacheAction: Boolean,
        ) : ContextMenu
    }

    sealed interface DeclineInviteMenu {
        data object Hidden : DeclineInviteMenu
        data class Shown(val roomSummary: RoomListRoomSummary) : DeclineInviteMenu
    }

    fun shouldDisplayActions(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT, HomeScreenTab.FEED, HomeScreenTab.PROFILE)
    }

    fun showDisplayMenuItems(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT)
    }

    fun shouldDisplayFilters(selectedHomeTab: HomeScreenTab): Boolean {
        return displayFilters && (selectedHomeTab == HomeScreenTab.NOTIFICATION)
    }
}

enum class SecurityBannerState {
    None,
    SetUpRecovery,
    RecoveryKeyConfirmation,
}

@Immutable
sealed interface RoomListContentState {
    data class Skeleton(val count: Int) : RoomListContentState
    data class Empty(
        val securityBannerState: SecurityBannerState,
    ) : RoomListContentState

    data class Rooms(
        val securityBannerState: SecurityBannerState,
        val fullScreenIntentPermissionsState: FullScreenIntentPermissionsState,
        val batteryOptimizationState: BatteryOptimizationState,
        val summaries: ImmutableList<RoomListRoomSummary>,
        val seenRoomInvites: ImmutableSet<RoomId>,
    ) : RoomListContentState
}

/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.invite.api.response.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.roomlist.impl.filters.RoomListFiltersState
import io.element.android.features.roomlist.impl.model.HomeScreenTab
import io.element.android.features.roomlist.impl.model.RoomListRoomSummary
import io.element.android.features.roomlist.impl.search.RoomListSearchState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class RoomListState(
    val matrixUser: MatrixUser,
    val showAvatarIndicator: Boolean,
    val hasNetworkConnection: Boolean,
    val snackbarMessage: SnackbarMessage?,
    val genericActionState: AsyncData<Unit>,
    val contextMenu: ContextMenu,
    val leaveRoomState: LeaveRoomState,
    val filtersState: RoomListFiltersState,
    val canReportBug: Boolean,
    val searchState: RoomListSearchState,
    val contentState: RoomListContentState,
    val channelContentState: ChannelListContentState,
    val allFeedsContentState: FeedListContentState,
    val myFeedsContentState: FeedListContentState,
    val resolvedChannelRoom: RoomId?,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val directLogoutState: DirectLogoutState,
    val eventSink: (RoomListEvents) -> Unit,

    val shouldShowNewRewardsIntimation: Boolean = true,
    val userRewards: ZeroUserRewards = ZeroUserRewards.empty()
) {
    //val displayFilters = contentState is RoomListContentState.Rooms
    val displayFilters = false //Hiding room list filters for now
    private val displayActions = true

    sealed interface ContextMenu {
        data object Hidden : ContextMenu
        data class Shown(
            val roomId: RoomId,
            val roomName: String?,
            val isDm: Boolean,
            val isFavorite: Boolean,
            val markAsUnreadFeatureFlagEnabled: Boolean,
            val eventCacheFeatureFlagEnabled: Boolean,
            val hasNewContent: Boolean,
        ) : ContextMenu
    }

    fun shouldDisplayActions(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT)
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
        val summaries: ImmutableList<RoomListRoomSummary>,
    ) : RoomListContentState
}

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.features.home.impl.channel.ChannelListState
import io.element.android.features.home.impl.feed.FeedListState
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.roomdirectory.impl.root.RoomDirectoryState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import kotlinx.collections.immutable.ImmutableList

data class HomeState(
    /**
     * The current user of this session, in case of multiple accounts, will contains 3 items, with the
     * current user in the middle.
     */
    val currentUserAndNeighbors: ImmutableList<MatrixUser>,
    val matrixUser: MatrixUser,
    val showAvatarIndicator: Boolean,
    val hasNetworkConnection: Boolean,
    val genericActionState: AsyncAction<Unit>,
    val currentHomeNavigationBarItem: HomeNavigationBarItem,
    val homeSpacesState: HomeSpacesState,
    val roomListState: RoomListState,
    val channelListState: ChannelListState,
    val feedListState: FeedListState,
    val walletContentState: WalletContentState,
    val roomDirectoryState: RoomDirectoryState,
    val snackbarMessage: SnackbarMessage?,
    val canReportBug: Boolean,
    val directLogoutState: DirectLogoutState,
    val shouldShowNewRewardsIntimation: Boolean = true,
    val userRewards: ZeroUserRewards = ZeroUserRewards.empty(),
    val showClaimRewardsSheet: Boolean,
    val claimRewardActionState: AsyncAction<String> = AsyncAction.Uninitialized,
    val eventSink: (HomeEvents) -> Unit,
) {
    private val displayActions = true

    fun shouldDisplayActions(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT, HomeScreenTab.CHANNEL, HomeScreenTab.FEED)
    }

    fun showDisplayMenuItems(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT, HomeScreenTab.CHANNEL, HomeScreenTab.FEED)
    }

//    val showNavigationBar = homeSpacesState.canCreateSpaces || homeSpacesState.spaceRooms.isNotEmpty()
    val showNavigationBar = false
}

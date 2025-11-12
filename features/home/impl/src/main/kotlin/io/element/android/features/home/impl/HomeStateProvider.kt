/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.channel.ChannelListState
import io.element.android.features.home.impl.channel.aChannelListState
import io.element.android.features.home.impl.feed.FeedListState
import io.element.android.features.home.impl.feed.aFeedListState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.roomlist.RoomListStateProvider
import io.element.android.features.home.impl.roomlist.aRoomListState
import io.element.android.features.home.impl.roomlist.aRoomsContentState
import io.element.android.features.home.impl.roomlist.generateRoomListRoomSummaryList
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.home.impl.spaces.aHomeSpacesState
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.home.impl.wallet.aWalletContentState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.logout.api.direct.aDirectLogoutState
import io.element.android.features.roomdirectory.impl.root.RoomDirectoryState
import io.element.android.features.roomdirectory.impl.root.aRoomDirectoryState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

open class HomeStateProvider : PreviewParameterProvider<HomeState> {
    override val values: Sequence<HomeState>
        get() = sequenceOf(
            aHomeState(),
            aHomeState(hasNetworkConnection = false),
            aHomeState(snackbarMessage = SnackbarMessage(CommonStrings.common_verification_complete)),
            aHomeState(
                isSpaceFeatureEnabled = true,
                roomListState = aRoomListState(
                    // Add more rooms to see the blur effect under the NavigationBar
                    contentState = aRoomsContentState(
                        summaries = generateRoomListRoomSummaryList(),
                    )
                ),
                // For the bottom nav bar to be visible in the preview, the user must be member of at least one space
                homeSpacesState = aHomeSpacesState(),
            ),
            aHomeState(
                isSpaceFeatureEnabled = true,
                currentHomeNavigationBarItem = HomeNavigationBarItem.Spaces,
            ),
        ) + RoomListStateProvider().values.map {
            aHomeState(roomListState = it)
        }
}

internal fun aHomeState(
    matrixUser: MatrixUser = MatrixUser(userId = UserId("@id:domain"), displayName = "User#1"),
    currentUserAndNeighbors: List<MatrixUser> = listOf(matrixUser),
    showAvatarIndicator: Boolean = false,
    hasNetworkConnection: Boolean = true,
    genericActionState: AsyncAction<Unit> = AsyncAction.Uninitialized,
    snackbarMessage: SnackbarMessage? = null,
    currentHomeNavigationBarItem: HomeNavigationBarItem = HomeNavigationBarItem.Chats,
    homeSpacesState: HomeSpacesState = aHomeSpacesState(),
    roomListState: RoomListState = aRoomListState(),
    channelListState: ChannelListState = aChannelListState(),
    feedListState: FeedListState = aFeedListState(),
    walletContentState: WalletContentState = aWalletContentState(),
    roomDirectoryState: RoomDirectoryState = aRoomDirectoryState(),
    canReportBug: Boolean = true,
    isSpaceFeatureEnabled: Boolean = false,
    directLogoutState: DirectLogoutState = aDirectLogoutState(),
    eventSink: (HomeEvents) -> Unit = {}
) = HomeState(
    currentUserAndNeighbors = currentUserAndNeighbors.toImmutableList(),
    matrixUser = matrixUser,
    showAvatarIndicator = showAvatarIndicator,
    hasNetworkConnection = hasNetworkConnection,
    genericActionState = genericActionState,
    currentHomeNavigationBarItem = currentHomeNavigationBarItem,
    roomListState = roomListState,
    channelListState = channelListState,
    feedListState = feedListState,
    walletContentState = walletContentState,
    roomDirectoryState = roomDirectoryState,
    snackbarMessage = snackbarMessage,
    canReportBug = canReportBug,
    directLogoutState = directLogoutState,
    homeSpacesState = homeSpacesState,
    isSpaceFeatureEnabled = isSpaceFeatureEnabled,
    showClaimRewardsSheet = false,
    eventSink = eventSink,
)

/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.channel.HomeChannelListContentView
import io.element.android.features.home.impl.components.ClaimRewardsSheet
import io.element.android.features.home.impl.components.HomeFabButton
import io.element.android.features.home.impl.components.HomeScreenTabView
import io.element.android.features.home.impl.components.HomeScreenTopBar
import io.element.android.features.home.impl.feed.HomeFeedListContentView
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.notification.HomeNotificationListContentView
import io.element.android.features.home.impl.roomlist.RoomListContentView
import io.element.android.features.home.impl.roomlist.RoomListContextMenu
import io.element.android.features.home.impl.roomlist.RoomListDeclineInviteMenu
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.RoomListMenuAction
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.search.RoomListSearchView
import io.element.android.features.home.impl.wallet.HomeWalletContent
import io.element.android.features.leaveroom.api.LeaveRoomView
import io.element.android.features.networkmonitor.api.ui.ConnectivityIndicatorContainer
import io.element.android.libraries.androidutils.throttler.FirstThrottler
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.state.StateBus
import io.element.android.support.zero.common.ui.component.feed.FeedMediaPreview

@Composable
fun HomeView(
    homeState: HomeState,
    onRoomClick: (RoomId) -> Unit,
    onSettingsClick: () -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onRoomSettingsClick: (roomId: RoomId) -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onReportRoomClick: (roomId: RoomId) -> Unit,
    onDeclineInviteAndBlockUser: (roomSummary: RoomListRoomSummary) -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onUserProfileClick: () -> Unit,
    onCreateFeedClick: () -> Unit,
    onSendWalletToken: () -> Unit,
    onReceiveWalletToken: () -> Unit,
    modifier: Modifier = Modifier,
    acceptDeclineInviteView: @Composable () -> Unit,
) {
    val roomListState: RoomListState = homeState.roomListState
    val coroutineScope = rememberCoroutineScope()
    val firstThrottler = remember { FirstThrottler(300, coroutineScope) }

    val resolvedChannelRoomId by remember(homeState.resolvedChannelRoom) {
        derivedStateOf { homeState.resolvedChannelRoom }
    }
    resolvedChannelRoomId?.let { onRoomClick(it) }

    ConnectivityIndicatorContainer(
        modifier = modifier,
        isOnline = homeState.hasNetworkConnection,
    ) { topPadding ->
        Box {
            if (roomListState.contextMenu is RoomListState.ContextMenu.Shown) {
                RoomListContextMenu(
                    contextMenu = roomListState.contextMenu,
                    canReportRoom = roomListState.canReportRoom,
                    eventSink = roomListState.eventSink,
                    onRoomSettingsClick = onRoomSettingsClick,
                    onReportRoomClick = onReportRoomClick,
                )
            }
            if (roomListState.declineInviteMenu is RoomListState.DeclineInviteMenu.Shown) {
                RoomListDeclineInviteMenu(
                    menu = roomListState.declineInviteMenu,
                    canReportRoom = roomListState.canReportRoom,
                    eventSink = roomListState.eventSink,
                    onDeclineAndBlockClick = onDeclineInviteAndBlockUser,
                )
            }

            LeaveRoomView(state = roomListState.leaveRoomState)

            HomeScaffold(
                state = homeState,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
                onOpenSettings = { if (firstThrottler.canHandle()) onSettingsClick() },
                onCreateRoomClick = { if (firstThrottler.canHandle()) onCreateRoomClick() },
                onMenuActionClick = onMenuActionClick,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                onUserProfileClick = onUserProfileClick,
                onCreateFeedClick = onCreateFeedClick,
                onSendWalletToken = onSendWalletToken,
                onReceiveWalletToken = onReceiveWalletToken,
                modifier = Modifier.padding(top = topPadding),
            )
            // This overlaid view will only be visible when state.displaySearchResults is true
            RoomListSearchView(
                state = roomListState.searchState,
                eventSink = roomListState.eventSink,
                hideInvitesAvatars = roomListState.hideInvitesAvatars,
                onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = topPadding)
                    .fillMaxSize()
                    .background(ElementTheme.colors.bgCanvasDefault)
            )
            acceptDeclineInviteView()

            if (homeState.genericActionState is AsyncAction.Loading) {
                ProgressDialog()
            }

            if (homeState.genericActionState is AsyncAction.Failure) {
                ErrorDialog(
                    content = stringResource(CommonStrings.error_unknown),
                    onSubmit = { homeState.eventSink(HomeEvents.HideError) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    state: HomeState,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomId) -> Unit,
    onOpenSettings: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onUserProfileClick: () -> Unit,
    onCreateFeedClick: () -> Unit,
    onSendWalletToken: () -> Unit,
    onReceiveWalletToken: () -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onRoomClick(room: RoomListRoomSummary) {
        onRoomClick(room.roomId)
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)
    val roomListState: RoomListState = state.roomListState

    val selectedNavigationTab = rememberSaveable { mutableStateOf(HomeScreenTab.CHAT) }
    val showFeedMediaPreview by remember(state.feedMediaPreviewState) {
        mutableStateOf(state.feedMediaPreviewState != AsyncAction.Uninitialized)
    }

    val claimRewardsSheetState = rememberModalBottomSheetState()

    Box {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeScreenTopBar(
                    matrixUser = state.matrixUser,
                    showAvatarIndicator = state.showAvatarIndicator,
                    areSearchResultsDisplayed = roomListState.searchState.isSearchActive,
                    onToggleSearch = { roomListState.eventSink(RoomListEvents.ToggleSearchResults) },
                    onMenuActionClick = onMenuActionClick,
                    onOpenSettings = onOpenSettings,
                    onOpenProfile = onUserProfileClick,
                    scrollBehavior = scrollBehavior,
                    displayMenuItems = state.showDisplayMenuItems(selectedNavigationTab.value),
                    displayFilters = roomListState.shouldDisplayFilters(selectedNavigationTab.value),
                    filtersState = roomListState.filtersState,
                    canReportBug = state.canReportBug,
                    shouldShowNewRewardsIntimation = state.shouldShowNewRewardsIntimation,
                    userRewards = state.userRewards,
                    onDismissRewardsTooltip = { immediate ->
                        state.eventSink(HomeEvents.DismissRewardsIntimation(immediate))
                    }
                )
            },
            content = { padding ->
                HomeScreenContent(
                    state = state,
                    selectedHomeScreenTab = selectedNavigationTab.value,
                    onSetUpRecoveryClick = onSetUpRecoveryClick,
                    onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                    onRoomClick = ::onRoomClick,
                    onCreateRoomClick = onCreateRoomClick,
                    onFeedClick = onFeedClick,
                    onFeedUserClick = onFeedUserClick,
                    onSendWalletToken = onSendWalletToken,
                    onReceiveWalletToken = onReceiveWalletToken,
                    modifier = Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            // Floating Action button
            if (state.shouldDisplayActions(selectedNavigationTab.value)) {
                HomeFabButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp),
                    onClick = {
                        when {
                            selectedNavigationTab.value == HomeScreenTab.CHAT -> onCreateRoomClick()
                            else -> onCreateFeedClick()
                        }
                    }
                )
            }

            // Home tab view
            HomeScreenTabView(
                selectedNavigationTab = selectedNavigationTab.value,
                onTabSelected = { tab ->
                    selectedNavigationTab.value = tab
                }
            )
        }
    }

    if (showFeedMediaPreview) {
        FeedMediaPreview(state.feedMediaPreviewState, onDismiss = {
            state.eventSink(HomeEvents.DismissFeedMedia)
        })
    }

    if (state.showClaimRewardsSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                StateBus.onRewardsClaimed()
            },
            sheetState = claimRewardsSheetState,
            dragHandle = null,
            content = {
                ClaimRewardsSheet(
                    userRewards = state.walletContentState.claimableRewards,
                    actionState = state.claimRewardActionState,
                    meowPrice = state.walletContentState.meowPrice,
                    onViewTransaction = { transaction ->
                        state.eventSink(HomeEvents.ViewWalletTransaction(transaction))
                    },
                    onClaimRewards = {
                        state.eventSink(HomeEvents.ClaimRewards)
                    },
                    onRewardsClaimed = {
                        state.eventSink(HomeEvents.RefreshWalletBalance)
                    }
                )
            }
        )
    }
}

@Composable
internal fun HomeScreenContent(
    state: HomeState,
    selectedHomeScreenTab: HomeScreenTab,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onSendWalletToken: () -> Unit = {},
    onReceiveWalletToken: () -> Unit = {},
    modifier: Modifier,
) {
    fun onNotificationClick(room: RoomListRoomSummary) {
        onRoomClick(room)
    }

    when (selectedHomeScreenTab) {
        HomeScreenTab.CHAT -> {
            RoomListContentView(
                modifier = modifier,
                contentState = state.roomListState.contentState,
                filtersState = state.roomListState.filtersState,
                hideInvitesAvatars = state.roomListState.hideInvitesAvatars,
                eventSink = state.roomListState.eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = onRoomClick,
                onCreateRoomClick = onCreateRoomClick,
                contentPadding = PaddingValues(0.dp)
            )
        }
        HomeScreenTab.CHANNEL -> {
            HomeChannelListContentView(
                contentState = state.channelContentState,
                eventSink = state.eventSink,
                modifier = modifier
            )
        }
        HomeScreenTab.FEED -> {
            HomeFeedListContentView(
                contentState = state.allFeedsContentState,
                feedMediaMap = state.feedMediaMap,
                feedLinkMetaDataMap = state.feedLinkMetaDataMap,
                eventSink = state.eventSink,
                zeroUserRewards = state.userRewards,
                isProfileFeedList = false,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                modifier = modifier
            )
        }
        HomeScreenTab.NOTIFICATION -> {
            HomeNotificationListContentView(
                contentState = state.roomListState.contentState,
                eventSink = state.roomListState.eventSink,
                onNotificationClick = ::onNotificationClick,
                modifier = modifier
            )
        }
        HomeScreenTab.WALLET -> {
            HomeWalletContent(
                modifier = modifier,
                state = state.walletContentState,
                onSendWalletToken = onSendWalletToken,
                onReceiveWalletToken = onReceiveWalletToken
            )
        }
        /*HomeScreenTab.PROFILE -> {
            HomeFeedListContentView(
                contentState = state.myFeedsContentState,
                feedMediaMap = state.feedMediaMap,
                feedLinkMetaDataMap = state.feedLinkMetaDataMap,
                eventSink = state.eventSink,
                zeroUserRewards = state.userRewards,
                isProfileFeedList = true,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                modifier = modifier
            )
        }*/
    }
}

internal fun RoomListRoomSummary.contentType() = displayType.ordinal

@PreviewsDayNight
@Composable
internal fun HomeViewPreview(@PreviewParameter(HomeStateProvider::class) state: HomeState) = ElementPreview {
    HomeView(
        homeState = state,
        onRoomClick = {},
        onSettingsClick = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onCreateRoomClick = {},
        onRoomSettingsClick = {},
        onReportRoomClick = {},
        onMenuActionClick = {},
        onFeedClick = {},
        onFeedUserClick = {},
        onUserProfileClick = {},
        onCreateFeedClick = {},
        onDeclineInviteAndBlockUser = {},
        acceptDeclineInviteView = {},
        onSendWalletToken = {},
        onReceiveWalletToken = {}
    )
}

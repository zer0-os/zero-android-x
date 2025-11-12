/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.api.AnnouncementService
import io.element.android.features.home.impl.channel.ChannelListState
import io.element.android.features.home.impl.feed.FeedListState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.home.impl.wallet.WalletEvents
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.indicator.api.IndicatorService
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.support.zero.common.extension.safeAsync
import io.element.android.support.zero.common.state.StateBus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Inject
class HomePresenter(
    private val client: MatrixClient,
    private val syncService: SyncService,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val homeSpacesPresenter: Presenter<HomeSpacesState>,
    private val roomListPresenter: Presenter<RoomListState>,
    private val channelListPresenter: Presenter<ChannelListState>,
    private val feedListPresenter: Presenter<FeedListState>,
    private val walletPresenter: Presenter<WalletContentState>,
    private val logoutPresenter: Presenter<DirectLogoutState>,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val featureFlagService: FeatureFlagService,
    private val sessionStore: SessionStore,
    private val announcementService: AnnouncementService,
) : Presenter<HomeState> {

    private val currentUserWithNeighborsBuilder = CurrentUserWithNeighborsBuilder()

    @Composable
    override fun present(): HomeState {
        val coroutineState = rememberCoroutineScope()
        val matrixUser by client.userProfile.collectAsState()
        val currentUserAndNeighbors by remember {
            combine(
                client.userProfile,
                sessionStore.sessionsFlow(),
                currentUserWithNeighborsBuilder::build,
            )
        }.collectAsState(initial = persistentListOf(matrixUser))
        val isOnline by syncService.isOnline.collectAsState()
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        val homeSpacesState = homeSpacesPresenter.present()
        val roomListState = roomListPresenter.present()
        val channelListState = channelListPresenter.present()
        val feedListState = feedListPresenter.present()
        val walletContentState = walletPresenter.present()

        val isSpaceFeatureEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.Space)
        }.collectAsState(initial = false)
        var currentHomeNavigationBarItemOrdinal by rememberSaveable { mutableIntStateOf(HomeNavigationBarItem.Chats.ordinal) }
        val currentHomeNavigationBarItem by remember {
            derivedStateOf {
                HomeNavigationBarItem.from(currentHomeNavigationBarItemOrdinal)
            }
        }

        var shouldShowRoomIntimation by rememberSaveable { mutableStateOf(true) }
        val shouldShowNewRewardsIntimation = client.shouldShowNewRewardsIntimation.collectAsState()
        val userRewards = client.userRewards.collectAsState()
        val claimableUserRewards = remember { mutableStateOf(ZeroUserRewards.empty()) }
        val showClaimRewardsSheet = StateBus.claimRewardsStateObservable.collectAsState(initial = false)
        val claimRewardsActionState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val baseGenericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        // Derived value (computed from child presenters)
        val derivedBaseState by remember(channelListState, feedListState, walletContentState) {
            derivedStateOf {
                val states = listOf(channelListState.genericActionState, feedListState.genericActionState, walletContentState.genericActionState)
                when {
                    states.any { it is AsyncAction.Loading } -> AsyncAction.Loading
                    states.any { it is AsyncAction.Failure } -> states.first { it is AsyncAction.Failure }
                    states.all { it is AsyncAction.Success } -> AsyncAction.Success(Unit)
                    else -> AsyncAction.Uninitialized
                }
            }
        }

        // Keep base in sync with derived automatically
        LaunchedEffect(derivedBaseState) {
            baseGenericActionState.value = derivedBaseState
        }
        LaunchedEffect(Unit) {
            // Force a refresh of the profile
            client.getUserProfile(true)
            // Fetch initial zero data
            fetchInitialData()
        }

        // Avatar indicator
        val showAvatarIndicator by indicatorService.showRoomListTopBarIndicator()
        val directLogoutState = logoutPresenter.present()

        fun handleEvent(event: HomeEvents) {
            when (event) {
                is HomeEvents.SelectHomeNavigationBarItem -> coroutineState.launch {
                    if (event.item == HomeNavigationBarItem.Spaces) {
                        announcementService.showAnnouncement(Announcement.Space)
                    }
                    currentHomeNavigationBarItemOrdinal = event.item.ordinal
                }
                is HomeEvents.DismissRewardsIntimation -> {
                    if (event.immediate) {
                        shouldShowRoomIntimation = false
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            shouldShowRoomIntimation = false
                        }, 3_000)
                    }
                }
                HomeEvents.ClaimRewards -> {
                    claimableUserRewards.value = userRewards.value
                    coroutineState.claimUserRewards(
                        matrixUser = matrixUser,
                        claimRewardsActionState = claimRewardsActionState,
                        refreshWallet = { walletContentState.eventSink(WalletEvents.RefreshWallet) }
                    )
                }
                HomeEvents.HideError -> baseGenericActionState.value = AsyncAction.Uninitialized
                is HomeEvents.SwitchToAccount -> coroutineState.launch {
                    sessionStore.setLatestSession(event.sessionId.value)
                }
            }
        }

        LaunchedEffect(homeSpacesState.spaceRooms.isEmpty()) {
            // If the last space is left, ensure that the Chat view is rendered.
            if (homeSpacesState.spaceRooms.isEmpty()) {
                currentHomeNavigationBarItemOrdinal = HomeNavigationBarItem.Chats.ordinal
            }
        }
        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

        return HomeState(
            currentUserAndNeighbors = currentUserAndNeighbors,
            matrixUser = matrixUser,
            showAvatarIndicator = showAvatarIndicator,
            hasNetworkConnection = isOnline,
            genericActionState = baseGenericActionState.value,
            currentHomeNavigationBarItem = currentHomeNavigationBarItem,
            homeSpacesState = homeSpacesState,
            roomListState = roomListState,
            channelListState = channelListState,
            feedListState = feedListState,
            snackbarMessage = snackbarMessage,
            canReportBug = canReportBug,
            directLogoutState = directLogoutState,
            isSpaceFeatureEnabled = isSpaceFeatureEnabled,
            shouldShowNewRewardsIntimation = shouldShowRoomIntimation && shouldShowNewRewardsIntimation.value,
            userRewards = userRewards.value,
            walletContentState = walletContentState.copy(claimableRewards = claimableUserRewards.value),
            showClaimRewardsSheet = showClaimRewardsSheet.value,
            claimRewardActionState = claimRewardsActionState.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.fetchInitialData() = launch {
        awaitAll(
            // Check zero thirdWeb wallet
            safeAsync { client.checkZeroThirdWebWallet() },
            // Fetch user rewards
            safeAsync { client.getUserRewards(shouldCheckRewardsIntimation = true) },
        )
    }

    private fun CoroutineScope.claimUserRewards(
        matrixUser: MatrixUser,
        claimRewardsActionState: MutableState<AsyncAction<String>>,
        refreshWallet: () -> Unit
    ) = launch {
        matrixUser.walletAddress?.let {
            claimRewardsActionState.value = AsyncAction.Loading
            client.claimRewards(it)
                .onSuccess { transaction ->
                    claimRewardsActionState.value = AsyncAction.Success(transaction)
                    client.getUserRewards()
                    refreshWallet()
                }
                .onFailure { error ->
                    claimRewardsActionState.value = AsyncAction.Failure(error)
                }
        }
    }
}

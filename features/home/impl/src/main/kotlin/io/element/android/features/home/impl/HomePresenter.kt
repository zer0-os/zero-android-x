/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.LocalContext
import io.element.android.features.home.impl.channel.ChannelListContentState
import io.element.android.features.home.impl.feed.FeedListContentState
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.channelId
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.features.home.impl.wallet.WalletTransactionsListState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.indicator.api.IndicatorService
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.isClaimableToken
import io.element.android.libraries.matrix.api.zero.wallet.tokenAmount
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.extension.safeAsync
import io.element.android.support.zero.common.extension.withIOScope
import io.element.android.support.zero.common.extension.withScope
import io.element.android.support.zero.common.state.StateBus
import io.element.android.support.zero.common.util.FeedItemMediaCache
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

private const val HOME_FEED_PAGE_SIZE = 15

class HomePresenter @Inject constructor(
    private val client: MatrixClient,
    private val syncService: SyncService,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val roomListPresenter: Presenter<RoomListState>,
    private val logoutPresenter: Presenter<DirectLogoutState>,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val featureFlagService: FeatureFlagService,
) : Presenter<HomeState> {

    private val channelRoomMap: MutableMap<String, RoomSummary> = mutableMapOf()

    private val _allFeeds: MutableList<ZeroFeed> = mutableListOf()
    private val _myFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): HomeState {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val matrixUser = client.userProfile.collectAsState()
        val isOnline by syncService.isOnline.collectAsState()
        val canReportBug = remember { rageshakeFeatureAvailability.isAvailable() }
        val roomListState = roomListPresenter.present()
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
        val showClaimRewardsSheet = StateBus.claimRewardsStateObservable.collectAsState(initial = false)
        val claimRewardsActionState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val resolvedChannelRoomId: MutableState<RoomId?> = remember { mutableStateOf(null) }

        val feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }

        val showWalletBalance = remember { mutableStateOf(true) }
        val userWalletBalance = remember { mutableDoubleStateOf(0.0) }
        val meowPrice: MutableState<ZeroMeowPrice?> = remember { mutableStateOf(null) }
        val walletTokensListState: MutableState<WalletTokensListState> = remember {
            mutableStateOf(WalletTokensListState.Skeleton(10))
        }
        val walletTokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?> = remember {
            mutableStateOf(null)
        }
        val walletTransactionsListState: MutableState<WalletTransactionsListState> = remember {
            mutableStateOf(WalletTransactionsListState.Skeleton(10))
        }
        val walletTransactionsPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?> =
            remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            // Force a refresh of the profile
            client.getUserProfile()
            // Fetch initial zero data
            fetchInitialData()
        }
        LaunchedEffect(Unit) {
            client.userProfile
                .mapNotNull { it.walletAddress }
                .distinctUntilChanged()
                .collectLatest {
                    fetchWalletData(
                        meowPrice, it, userWalletBalance,
                        walletTokensListState, walletTransactionsListState,
                        walletTokenPaginationParams, walletTransactionsPaginationParams
                    )
                }
        }
        // Avatar indicator
        val showAvatarIndicator by indicatorService.showRoomListTopBarIndicator()
        val directLogoutState = logoutPresenter.present()

        fun handleEvents(event: HomeEvents) {
            when (event) {
                is HomeEvents.SelectHomeNavigationBarItem -> {
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
                HomeEvents.HideError -> genericActionState.value = AsyncAction.Uninitialized
                is HomeEvents.OpenChannel -> coroutineScope.openChannel(event.channel, resolvedChannelRoomId, genericActionState)
                is HomeEvents.LoadMoreFeeds -> {
                    _allFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreHomeFeeds(event.followingFeeds, event.currentFeeds.size)
                }
                is HomeEvents.RefreshFeeds -> coroutineScope.forceRefreshHomeFeeds(event.followingFeeds)
                is HomeEvents.LoadMoreMyFeeds -> {
                    _myFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreMyFeeds(event.currentFeeds.size)
                }
                HomeEvents.RefreshMyFeeds -> coroutineScope.forceRefreshMyFeeds()
                is HomeEvents.AddMeowToFeed -> coroutineScope.addMeowToFeed(event.feed, event.meowCount)
                is HomeEvents.LoadFeedMedia -> {
                    coroutineScope.loadFeedMediaPreview(event.mediaId, feedMediaPreviewActionState)
                }
                HomeEvents.DismissFeedMedia -> feedMediaPreviewActionState.value = AsyncAction.Uninitialized
                is HomeEvents.LoadMoreTokens -> {
                    matrixUser.value.walletAddress?.let { address ->
                        coroutineScope.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = event.currentTokens,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
                is HomeEvents.LoadMoreTransactions -> {
                    matrixUser.value.walletAddress?.let { address ->
                        coroutineScope.loadMoreWalletTransactions(
                            walletAddress = address,
                            currentList = event.currentTransactions,
                            walletTransactionsListState = walletTransactionsListState,
                            transactionPaginationParams = walletTransactionsPaginationParams
                        )
                    }
                }
                is HomeEvents.ViewWalletTransaction -> {
                    coroutineScope.loadWalletTransaction(
                        event.transactionId, context, genericActionState
                    )
                }
                HomeEvents.ToggleWalletBalance -> showWalletBalance.value = !showWalletBalance.value
                HomeEvents.ClaimRewards -> coroutineScope
                    .claimUserRewards(matrixUser.value, claimRewardsActionState)
                HomeEvents.RefreshWalletBalance -> {
                    matrixUser.value.walletAddress?.let { address ->
                        val currentList = (walletTokensListState.value as? WalletTokensListState.Tokens)
                            ?.tokens ?: emptyList()
                        coroutineScope.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = currentList,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
            }
        }

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

        val channelContentState = channelListContentState()
        createChannelRoomMap(
            (channelContentState as? ChannelListContentState.Channels)?.channels.orEmpty()
        )

        val allFeedsContentState = allFeedsListContentState()
        val myFeedsContentState = allMyFeedsListContentState()

        /*val feedMediaMap = rememberSaveable(
            saver = mapSaver(
                save = { it.toMap() },
                restore = {
                    mutableStateMapOf<String, FeedMedia>().apply {
                        putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
                    }
                }
            )
        ) { mutableStateMapOf() }*/
        val feedMediaMap = remember { mutableStateMapOf<String, FeedMedia>() }
        val feedLinkMetaDataMap = remember { mutableStateMapOf<String, ZeroLinkPreview>() }

        LaunchedEffect(Unit) {
            feedMediaMap.putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
            feedLinkMetaDataMap.putAll(FeedItemMediaCache.getCachedFeedItemLinkMetaDataMap())
        }
        val allCombinedFeeds = extractFeedsToFetchData(allFeedsContentState, myFeedsContentState)
        fetchFeedMediaIfRequired(allCombinedFeeds, feedMediaMap)
        fetchLinksMetaDataIfRequired(allCombinedFeeds, feedLinkMetaDataMap)

        return HomeState(
            matrixUser = matrixUser.value,
            showAvatarIndicator = showAvatarIndicator,
            hasNetworkConnection = isOnline,
            genericActionState = genericActionState.value,
            currentHomeNavigationBarItem = currentHomeNavigationBarItem,
            roomListState = roomListState,
            channelContentState = channelContentState,
            allFeedsContentState = allFeedsContentState,
            myFeedsContentState = myFeedsContentState,
            feedMediaMap = feedMediaMap,
            feedLinkMetaDataMap = feedLinkMetaDataMap,
            resolvedChannelRoom = resolvedChannelRoomId.value,
            snackbarMessage = snackbarMessage,
            canReportBug = canReportBug,
            directLogoutState = directLogoutState,
            isSpaceFeatureEnabled = isSpaceFeatureEnabled,
            shouldShowNewRewardsIntimation = shouldShowRoomIntimation && shouldShowNewRewardsIntimation.value,
            userRewards = userRewards.value,
            feedMediaPreviewState = feedMediaPreviewActionState.value,
            walletContentState = WalletContentState(
                userName = matrixUser.value.displayName ?: "",
                showWalletBalance = showWalletBalance.value,
                walletBalance = userWalletBalance.doubleValue,
                tokensListState = walletTokensListState.value,
                transactionsListState = walletTransactionsListState.value,
                tokensPaginationParams = walletTokenPaginationParams.value,
                transactionsPaginationParams = walletTransactionsPaginationParams.value,
                meowPrice = meowPrice.value,
                eventSink = ::handleEvents
            ),
            showClaimRewardsSheet = showClaimRewardsSheet.value,
            claimRewardActionState = claimRewardsActionState.value,
            eventSink = ::handleEvents,
        )
    }

    private fun CoroutineScope.fetchInitialData() = launch {
        awaitAll(
            // Check zero thirdWeb wallet
            safeAsync { client.checkZeroThirdWebWallet() },
            // Fetch user rewards
            safeAsync { client.getUserRewards(shouldCheckRewardsIntimation = true) },
            // Fetch home channels
            safeAsync { client.getUserZIds() },
            // Fetch all home feeds
            safeAsync { client.fetchAllFeeds(followingFeeds = true, limit = HOME_FEED_PAGE_SIZE, skip = 0) },
            // Fetch all my feeds
            safeAsync { client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = 0) },
        )
    }

    @Composable
    private fun channelListContentState(): ChannelListContentState {
        val homeChannelsState by produceState(initialValue = AsyncData.Loading()) {
            client.userZIds.collect {
                value = AsyncData.Success(it)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (homeChannelsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                homeChannelsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> ChannelListContentState.Empty
            showSkeleton -> ChannelListContentState.Skeleton(20)
            else -> {
                val mappedChannels = homeChannelsState.dataOrNull()
                    .orEmpty()
                    .sorted()
                    .map { HomeScreenChannel(channelFullName = it) }
                    .distinctBy { it.channelId() }
                    .toPersistentList()
                ChannelListContentState.Channels(mappedChannels)
            }
        }
    }

    @Composable
    private fun allFeedsListContentState(): FeedListContentState {
        val homeFeedsState by produceState(initialValue = AsyncData.Loading()) {
            client.allFeeds.collect {
                val feeds: List<ZeroFeed> = mutableListOf<ZeroFeed>().apply {
                    addAll(_allFeeds)
                    addAll(it)
                }.distinctBy { it.id }
                value = AsyncData.Success(feeds)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (homeFeedsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                homeFeedsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> FeedListContentState.Empty
            showSkeleton -> FeedListContentState.Skeleton(HOME_FEED_PAGE_SIZE)
            else -> {
                val mappedAllFeeds = homeFeedsState.dataOrNull()
                    .orEmpty()
                    .toPersistentList()
                FeedListContentState.Feeds(mappedAllFeeds)
            }
        }
    }

    @Composable
    private fun allMyFeedsListContentState(): FeedListContentState {
        val myFeedsState by produceState(initialValue = AsyncData.Loading()) {
            client.allMyFeeds.collect {
                val feeds: List<ZeroFeed> = mutableListOf<ZeroFeed>().apply {
                    addAll(_myFeeds)
                    addAll(it)
                }.distinctBy { it.id }
                value = AsyncData.Success(feeds)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (myFeedsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                myFeedsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> FeedListContentState.Empty
            showSkeleton -> FeedListContentState.Skeleton(HOME_FEED_PAGE_SIZE)
            else -> {
                val mappedMyFeeds = myFeedsState.dataOrNull()
                    .orEmpty()
                    .toPersistentList()
                FeedListContentState.Feeds(mappedMyFeeds)
            }
        }
    }

    private fun createChannelRoomMap(channels: List<HomeScreenChannel>) = withScope(Dispatchers.IO) {
        for (channel in channels) {
            channel.channelId()?.let { channelId ->
                val roomSummary = client.getRoomSummaryFlow(RoomAlias(channelId).toRoomIdOrAlias())
                    .firstOrNull()
                    ?.getOrNull()
                roomSummary?.let { summary ->
                    channel.notificationsCount = summary.info.numUnreadMessages.toInt()
                    channelRoomMap.put(channelId, summary)
                }
            }
        }
    }

    private fun CoroutineScope.openChannel(
        channel: HomeScreenChannel,
        resolvedChannelRoomId: MutableState<RoomId?>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Uninitialized
        val channelId = channel.channelId() ?: return@launch
        channelRoomMap[channelId]?.let { roomSummary ->
            resolvedChannelRoomId.value = roomSummary.roomId
            return@launch
        }
        genericActionState.value = AsyncAction.Loading
        client.resolveRoomAlias(RoomAlias(channelId)).getOrNull()?.getOrNull()?.roomId?.let { roomId ->
            resolvedChannelRoomId.value = roomId
            genericActionState.value = AsyncAction.Success(Unit)
            return@launch
        }
        client.joinZeroChannel(channelId)
            .onSuccess { roomId ->
                roomId?.let {
                    genericActionState.value = AsyncAction.Success(Unit)
                    resolvedChannelRoomId.value = RoomId(roomId)
                } ?: run {
                    genericActionState.value = AsyncAction.Failure(Throwable("RoomId not found"))
                }
            }
            .onFailure { failure ->
                genericActionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.loadMoreHomeFeeds(followingFeedsOnly: Boolean, skip: Int) = launch {
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = skip)
    }

    private fun CoroutineScope.forceRefreshHomeFeeds(followingFeedsOnly: Boolean) = launch {
        _allFeeds.clear()
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = 0)
    }

    private fun CoroutineScope.loadMoreMyFeeds(skip: Int) = launch {
        client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = skip)
    }

    private fun CoroutineScope.forceRefreshMyFeeds() = launch {
        _myFeeds.clear()
        client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = 0)
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int) = launch {
        client.addMeowToFeed(feed, meowCount)
    }

    private fun extractFeedsToFetchData(
        allFeedsContentState: FeedListContentState,
        myFeedsContentState: FeedListContentState,
    ): List<ZeroFeed> {
        val allFeeds = (allFeedsContentState as? FeedListContentState.Feeds)?.feeds ?: emptyList()
        val myFeeds = (myFeedsContentState as? FeedListContentState.Feeds)?.feeds ?: emptyList()
        val feeds = mutableListOf<ZeroFeed>().apply {
            addAll(allFeeds)
            addAll(myFeeds)
        }.distinctBy { it.id }.toList()
        return feeds
    }

    private fun fetchFeedMediaIfRequired(
        feeds: List<ZeroFeed>,
        feedMediaMap: SnapshotStateMap<String, FeedMedia>
    ) {
        withIOScope {
            coroutineScope {
                val feedsToFetch = feeds.mapNotNull { feed ->
                    val mediaId = feed.media?.id ?: return@mapNotNull null
                    if (feedMediaMap.contains(feed.id) || FeedItemMediaCache.containsMedia(feed.id)) return@mapNotNull null
                    else feed
                }
                val results = feedsToFetch.map { feed ->
                    async { feed.id to client.fetchFeedMedia(feed.media!!.id) }
                }.awaitAll()
                results.forEach { (feedId, media) ->
                    media.getOrNull()?.let { feedMedia ->
                        FeedItemMediaCache.addFeedMedia(feedId, feedMedia)
                        feedMediaMap[feedId] = feedMedia
                    }
                }
            }
        }
    }

    private fun fetchLinksMetaDataIfRequired(
        feeds: List<ZeroFeed>,
        feedLinkMetaDataMap: SnapshotStateMap<String, ZeroLinkPreview>
    ) {
        withIOScope {
            coroutineScope {
                val feedsToFetch = feeds.mapNotNull { feed ->
                    val availableYoutubeUrl = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text) ?: return@mapNotNull null
                    if (feedLinkMetaDataMap.contains(feed.id) || FeedItemMediaCache.containsUrlMetaData(feed.id)) return@mapNotNull null
                    else feed
                }
                val results = feedsToFetch.map { feed ->
                    val url = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text)!!
                    async { feed.id to client.fetchUrlMetaData(url) }
                }.awaitAll()
                results.forEach { (feedId, urlMetaData) ->
                    urlMetaData.getOrNull()?.let { metaData ->
                        FeedItemMediaCache.addLinkMetaData(feedId, metaData)
                        feedLinkMetaDataMap[feedId] = metaData
                    }
                }
            }
        }
    }

    private fun CoroutineScope.loadFeedMediaPreview(
        mediaId: String, feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>>
    ) = launch {
        feedMediaPreviewActionState.value = AsyncAction.Loading
        client.fetchFeedMedia(mediaId, isPreview = false)
            .onSuccess { media ->
                media?.let {
                    feedMediaPreviewActionState.value = AsyncAction.Success(it)
                } ?: run {
                    feedMediaPreviewActionState.value = AsyncAction.Failure(Throwable("Media not found."))
                }
            }
            .onFailure { failure ->
                feedMediaPreviewActionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.fetchWalletData(
        meowPrice: MutableState<ZeroMeowPrice?>,
        walletAddress: String,
        userWalletBalance: MutableDoubleState,
        walletTokensListState: MutableState<WalletTokensListState>,
        walletTransactionsListState: MutableState<WalletTransactionsListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        transactionPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?>
    ) = launch(context = Dispatchers.IO) {
        val results = awaitAll(
            async { client.getMeowPrice() },
            async { client.getWalletTokens(walletAddress, tokenPaginationParams.value) },
            async { client.getWalletTransactions(walletAddress, transactionPaginationParams.value) },
        )
        (results[0] as? Result<ZeroMeowPrice>)?.let {
            meowPrice.value = it.getOrNull()
        }
        (results[1] as? Result<ZeroWalletTokensResponse>)?.let {
            it.onSuccess { result ->
                val tokensList = result.tokens
                setWalletBalance(tokensList, meowPrice.value, userWalletBalance)
                walletTokensListState.value = WalletTokensListState.Tokens(
                    tokensList
                        .distinctBy { token -> token.tokenAddress }
                        .toPersistentList()
                )
                tokenPaginationParams.value = result.paginationParams
            }.onFailure {
                walletTokensListState.value = WalletTokensListState.Empty
            }
        }
        (results[2] as? Result<ZeroWalletTransactionsResponse>)?.let {
            it.onSuccess { result ->
                walletTransactionsListState.value = WalletTransactionsListState.Transactions(
                    result.transactions
                        .distinctBy { transaction -> transaction.hash }
                        .toPersistentList()
                )
                transactionPaginationParams.value = result.paginationParams
            }.onFailure {
                walletTransactionsListState.value = WalletTransactionsListState.Empty
            }
        }
    }

    private fun CoroutineScope.loadMoreWalletTokens(
        walletAddress: String,
        currentList: List<ZeroWalletToken>,
        walletTokensListState: MutableState<WalletTokensListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        meowPrice: MutableState<ZeroMeowPrice?>,
        userWalletBalance: MutableDoubleState,
    ) = launch {
        client.getWalletTokens(walletAddress, tokenPaginationParams.value)
            .onSuccess {
                val newList = mutableListOf<ZeroWalletToken>().apply {
                    addAll(currentList)
                    addAll(it.tokens)
                }.distinctBy { token -> token.tokenAddress }
                setWalletBalance(newList, meowPrice.value, userWalletBalance)
                walletTokensListState.value = WalletTokensListState.Tokens(newList.toPersistentList())
                tokenPaginationParams.value = it.paginationParams
            }
            .onFailure {
                //Failed to load tokens next page
                walletTokensListState.value = WalletTokensListState.Tokens(currentList.toPersistentList())
            }
    }

    private fun CoroutineScope.loadMoreWalletTransactions(
        walletAddress: String,
        currentList: List<ZeroWalletTransaction>,
        walletTransactionsListState: MutableState<WalletTransactionsListState>,
        transactionPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?>
    ) = launch {
        client.getWalletTransactions(walletAddress, transactionPaginationParams.value)
            .onSuccess {
                val newList = mutableListOf<ZeroWalletTransaction>().apply {
                    addAll(currentList)
                    addAll(it.transactions)
                }.distinctBy { trans -> trans.hash }
                walletTransactionsListState.value = WalletTransactionsListState.Transactions(newList.toPersistentList())
                transactionPaginationParams.value = it.paginationParams
            }
            .onFailure {
                //Failed to load transactions next page
                walletTransactionsListState.value = WalletTransactionsListState.Transactions(currentList.toPersistentList())
            }
    }

    private fun CoroutineScope.loadWalletTransaction(
        transactionId: String,
        context: Context,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.getTransactionReceipt(transactionId)
            .onSuccess {
                genericActionState.value = AsyncAction.Success(Unit)
                context.openExternalUri(it.blockExplorerUrl)
            }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun setWalletBalance(tokensList: List<ZeroWalletToken>,
                                 meowPrice: ZeroMeowPrice?,
                                 userWalletBalance: MutableDoubleState
    ) {
        val meowPrice = meowPrice ?: return
        val meowTokens = tokensList.filter { it.isClaimableToken }
        val userBalance = ZeroWalletUtil.getWalletBalance(
            meowTokenAmount = meowTokens.sumOf { it.tokenAmount },
            meowPrice = meowPrice
        )
        userWalletBalance.doubleValue = userBalance
    }

    private fun CoroutineScope.claimUserRewards(matrixUser: MatrixUser,
                                                claimRewardsActionState: MutableState<AsyncAction<String>>
    ) = launch {
        matrixUser.walletAddress?.let {
            claimRewardsActionState.value = AsyncAction.Loading
            client.claimRewards(it)
                .onSuccess { transaction ->
                    claimRewardsActionState.value = AsyncAction.Success(transaction)
                    client.getUserRewards()
                }
                .onFailure { error ->
                    claimRewardsActionState.value = AsyncAction.Failure(error)
                }
        }
    }
}

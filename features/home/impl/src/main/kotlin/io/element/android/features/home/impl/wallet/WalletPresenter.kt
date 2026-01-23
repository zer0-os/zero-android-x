/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.features.home.impl.model.SelectedStakePool
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.extensions.toLocalizedDoubleOrZero
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingConfig
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingStatus
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUserRewardsInfo
import io.element.android.libraries.matrix.api.zero.staking.ZeroTokenAddress
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFT
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFTsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenBalance
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import io.element.android.libraries.matrix.api.zero.wallet.isClaimableToken
import io.element.android.libraries.matrix.api.zero.wallet.meowPrice
import io.element.android.libraries.matrix.api.zero.wallet.tokenPrice
import io.element.android.support.zero.common.extension.withIOScope
import io.element.android.support.zero.common.util.wallet.WalletChainsUtil
import io.element.android.support.zero.common.util.wallet.ZeroStakingUtil
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@Inject
class WalletPresenter(
    private val client: MatrixClient,
) : Presenter<WalletContentState> {

    @Composable
    override fun present(): WalletContentState {
        val coroutineState = rememberCoroutineScope()
        val matrixUser by client.userProfile.collectAsState()

        val walletTransactionUrlState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

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
        val walletNFTsListState: MutableState<WalletNFTsListState> = remember {
            mutableStateOf(WalletNFTsListState.Skeleton(10))
        }
        val walletNFTsPaginationParams: MutableState<ZeroWalletTokensPaginationParams?> = remember {
            mutableStateOf(null)
        }
        val walletStakingContent: MutableState<List<HomeStakePool>> = remember { mutableStateOf(emptyList()) }
        val selectedStakePool: MutableState<SelectedStakePool?> = remember { mutableStateOf(null) }
        val showWalletStakingSheet = remember { mutableStateOf(false) }
        val walletStakeActionState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val fetchWalletData: (walletAddress: String) -> Unit = { walletAddress ->
            coroutineState.fetchWalletData(
                meowPrice, walletAddress, userWalletBalance,
                walletStakingContent, walletTokensListState, walletTransactionsListState, walletNFTsListState,
                walletTokenPaginationParams, walletTransactionsPaginationParams, walletNFTsPaginationParams,
                true
            )
        }

        val fetchStakeData: (walletAddress: String, refreshAllData: Boolean) -> Unit = { walletAddress, refreshAllData ->
            fetchStakingData(
                stakePoolsContent = walletStakingContent,
                meowPrice = meowPrice.value,
                userAddress = walletAddress,
                refreshAllData = refreshAllData,
                onRefreshAllData = { pool ->
                    coroutineState.fetchPoolData(
                        pool = pool,
                        selectedStakePoolState = selectedStakePool,
                        genericActionState = genericActionState,
                        showWalletStakingSheetState = showWalletStakingSheet
                    )
                }
            )
        }

        fun handleEvent(event: WalletEvents) {
            when (event) {
                is WalletEvents.LoadMoreTokens -> {
                    matrixUser.walletAddress?.let { address ->
                        coroutineState.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = event.currentTokens,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
                is WalletEvents.LoadMoreTransactions -> {
                    matrixUser.walletAddress?.let { address ->
                        coroutineState.loadMoreWalletTransactions(
                            walletAddress = address,
                            currentList = event.currentTransactions,
                            walletTransactionsListState = walletTransactionsListState,
                            transactionPaginationParams = walletTransactionsPaginationParams
                        )
                    }
                }
                is WalletEvents.LoadMoreNFTs -> {
                    matrixUser.walletAddress?.let { address ->
                        coroutineState.loadMoreWalletNFTs(
                            walletAddress = address,
                            currentList = event.currentNFTs,
                            walletNFTsListState = walletNFTsListState,
                            nftPaginationParams = walletNFTsPaginationParams
                        )
                    }
                }
                is WalletEvents.ViewWalletTransaction -> {
                    coroutineState.loadWalletTransaction(
                        event.transactionId, event.chainId, walletTransactionUrlState, genericActionState
                    )
                }
                WalletEvents.OnWalletTransactionViewed ->
                    walletTransactionUrlState.value = AsyncAction.Uninitialized
                WalletEvents.ToggleWalletBalance -> showWalletBalance.value = !showWalletBalance.value
                WalletEvents.RefreshWalletBalance -> {
                    matrixUser.walletAddress?.let { address ->
                        val currentList = (walletTokensListState.value as? WalletTokensListState.Tokens)
                            ?.tokens ?: emptyList()
                        coroutineState.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = currentList,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
                is WalletEvents.StakePoolSelected -> {
                    selectedStakePool.value = null
                    coroutineState.fetchPoolData(
                        pool = event.pool,
                        selectedStakePoolState = selectedStakePool,
                        genericActionState = genericActionState,
                        showWalletStakingSheetState = showWalletStakingSheet
                    )
                }
                is WalletEvents.StakeAmount -> {
                    selectedStakePool.value?.let {
                        coroutineState.stakeAmount(it, event.amount, walletStakeActionState) {
                            matrixUser.walletAddress?.let { address -> fetchStakeData(address, false) }
                        }
                    }
                }
                is WalletEvents.UnstakeAmount -> {
                    selectedStakePool.value?.let {
                        coroutineState.unstakeAmount(it, event.amount, walletStakeActionState) {
                            matrixUser.walletAddress?.let { address -> fetchStakeData(address, false) }
                        }
                    }
                }
                WalletEvents.DismissStakingSheet -> {
                    showWalletStakingSheet.value = false
                    walletStakeActionState.value = AsyncAction.Uninitialized
                    selectedStakePool.value = null
                    client.userProfile.value.walletAddress?.let { walletAddress ->
                        fetchWalletData(walletAddress)
                    }
                }
                WalletEvents.ClaimStakingRewards -> {
                    selectedStakePool.value?.poolInfo?.let { pool ->
                        coroutineState.claimStakingRewards(
                            pool = pool,
                            genericActionState = genericActionState,
                            onDone = {
                                matrixUser.walletAddress?.let { address -> fetchStakeData(address, true) }
                            }
                        )
                    }
                }
                WalletEvents.RefreshWallet -> client.userProfile.value.walletAddress?.let { walletAddress ->
                    fetchWalletData(walletAddress)
                }
            }
        }

        LaunchedEffect(Unit) {
            client.userProfile
                .mapNotNull { it.walletAddress }
                .distinctUntilChanged()
                .collectLatest { fetchWalletData(it) }
        }

        return WalletContentState(
            genericActionState = genericActionState.value,
            userName = matrixUser.displayName ?: "",
            showWalletBalance = showWalletBalance.value,
            walletBalance = userWalletBalance.doubleValue,
            walletTransactionUrlState = walletTransactionUrlState.value,
            claimableRewards = ZeroUserRewards.empty(),
            tokensListState = walletTokensListState.value,
            transactionsListState = walletTransactionsListState.value,
            nftsListState = walletNFTsListState.value,
            tokensPaginationParams = walletTokenPaginationParams.value,
            transactionsPaginationParams = walletTransactionsPaginationParams.value,
            nftsPaginationParams = walletNFTsPaginationParams.value,
            meowPrice = meowPrice.value,
            stakePools = walletStakingContent.value,
            selectedPool = selectedStakePool.value,
            showStakingSheet = showWalletStakingSheet.value,
            walletStakeActionState = walletStakeActionState.value,
            eventSink = ::handleEvent
        )
    }

    private fun CoroutineScope.fetchWalletData(
        meowPrice: MutableState<ZeroMeowPrice?>,
        walletAddress: String,
        userWalletBalance: MutableDoubleState,
        stakePoolsContent: MutableState<List<HomeStakePool>>,
        walletTokensListState: MutableState<WalletTokensListState>,
        walletTransactionsListState: MutableState<WalletTransactionsListState>,
        walletNFTsListState: MutableState<WalletNFTsListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        transactionPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?>,
        nftPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        forceRefresh: Boolean,
    ) = launch(context = Dispatchers.IO) {
        val results = awaitAll(
            async { client.getMeowPrice() },
            async {
                client.getWalletTokens(
                    walletAddress = walletAddress,
                    paginationParams = if (forceRefresh) null else tokenPaginationParams.value
                )
            },
            async {
                client.getWalletTransactions(
                    walletAddress = walletAddress,
                    paginationParams = if (forceRefresh) null else transactionPaginationParams.value
                )
            },
            async {
                client.getWalletNFTs(
                    walletAddress = walletAddress,
                    paginationParams = if (forceRefresh) null else nftPaginationParams.value
                )
            },
        )
        (results[0] as? Result<ZeroMeowPrice>)?.let {
            meowPrice.value = it.getOrNull()
            fetchStakingData(stakePoolsContent, meowPrice.value, walletAddress)
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
        (results[3] as? Result<ZeroWalletNFTsResponse>)?.let {
            it.onSuccess { result ->
                if (result.nfts.isEmpty()) {
                    walletNFTsListState.value = WalletNFTsListState.Empty
                } else {
                    walletNFTsListState.value = WalletNFTsListState.NFTs(
                        result.nfts
                            .distinctBy { nft -> nft.id }
                            .toPersistentList()
                    )
                }
                nftPaginationParams.value = result.paginationParams
            }.onFailure {
                walletNFTsListState.value = WalletNFTsListState.Empty
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
        client.getWalletTokens(
            walletAddress = walletAddress,
            paginationParams = tokenPaginationParams.value
        ).onSuccess {
            val newList = mutableListOf<ZeroWalletToken>().apply {
                addAll(currentList)
                addAll(it.tokens)
            }.distinctBy { token -> token.tokenAddress }
            setWalletBalance(newList, meowPrice.value, userWalletBalance)
            walletTokensListState.value = WalletTokensListState.Tokens(newList.toPersistentList())
            tokenPaginationParams.value = it.paginationParams
        }.onFailure {
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
        client.getWalletTransactions(
            walletAddress = walletAddress,
            paginationParams = transactionPaginationParams.value
        ).onSuccess {
            val newList = mutableListOf<ZeroWalletTransaction>().apply {
                addAll(currentList)
                addAll(it.transactions)
            }.distinctBy { trans -> trans.hash }
            walletTransactionsListState.value = WalletTransactionsListState.Transactions(newList.toPersistentList())
            transactionPaginationParams.value = it.paginationParams
        }.onFailure {
            //Failed to load transactions next page
            walletTransactionsListState.value = WalletTransactionsListState.Transactions(currentList.toPersistentList())
        }
    }

    private fun CoroutineScope.loadMoreWalletNFTs(
        walletAddress: String,
        currentList: List<ZeroWalletNFT>,
        walletNFTsListState: MutableState<WalletNFTsListState>,
        nftPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>
    ) = launch {
        client.getWalletNFTs(
            walletAddress = walletAddress,
            paginationParams = nftPaginationParams.value
        ).onSuccess {
            val newList = mutableListOf<ZeroWalletNFT>().apply {
                addAll(currentList)
                addAll(it.nfts)
            }.distinctBy { nft -> nft.id }
            walletNFTsListState.value = WalletNFTsListState.NFTs(newList.toPersistentList())
            nftPaginationParams.value = it.paginationParams
        }.onFailure {
            walletNFTsListState.value = WalletNFTsListState.NFTs(currentList.toPersistentList())
        }
    }

    private fun CoroutineScope.loadWalletTransaction(
        transactionId: String,
        chainId: Long?,
        walletTransactionUrlState: MutableState<AsyncAction<String>>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.getTransactionReceipt(transactionId, chainId)
            .onSuccess {
                genericActionState.value = AsyncAction.Success(Unit)
                walletTransactionUrlState.value = AsyncAction.Success(it.blockExplorerUrl)
            }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun setWalletBalance(
        tokensList: List<ZeroWalletToken>,
        meowPrice: ZeroMeowPrice?,
        userWalletBalance: MutableDoubleState
    ) {
        val totalBalance = tokensList
            .asSequence()
            .filter { it.isClaimableToken }
            .sumOf { token ->
                val isZChain = WalletChainsUtil.isZChain(token.chainId)
                if (isZChain) {
                    meowPrice?.let { price -> token.meowPrice(meowPrice) } ?: 0.0
                } else {
                    token.tokenPrice
                }
            }
            .toBigDecimal()
            .setScale(2, RoundingMode.FLOOR)
            .toDouble()

        userWalletBalance.doubleValue = totalBalance
    }

    private fun fetchStakingData(
        stakePoolsContent: MutableState<List<HomeStakePool>>,
        meowPrice: ZeroMeowPrice?,
        userAddress: String,
        refreshAllData: Boolean = false,
        onRefreshAllData: (HomeStakePool) -> Unit = {},
    ) {
        val price = meowPrice ?: return
        val stakePools = ZeroStakingUtil.stakePools

        withIOScope {
            coroutineScope {
                val pools = stakePools.map { stakePool ->
                    async {
                        val poolAddress = stakePool.address
                        val chainId = stakePool.chainId

                        val (totalStakedResult, stakingConfigResult, stakeStatusResult, rewardsInfoResult, stakeTokenResult) = awaitAll(
                            async { client.getTotalStaked(poolAddress, chainId) },
                            async { client.getStakingConfig(poolAddress, chainId) },
                            async { client.getStakerStatusInfo(userAddress, poolAddress, chainId) },
                            async { client.getStakeRewardsInfo(userAddress, poolAddress, chainId) },
                            async { client.getStakingToken(poolAddress, chainId) }
                        )

                        if (listOf(totalStakedResult, stakingConfigResult, stakeStatusResult, rewardsInfoResult, stakeTokenResult).all { it.isSuccess }) {
                            val totalStaked = (totalStakedResult as Result<String>).getOrNull() ?: return@async null
                            val stakingConfig = (stakingConfigResult as Result<ZeroStakingConfig>).getOrNull() ?: return@async null
                            val stakeStatus = (stakeStatusResult as Result<ZeroStakingStatus>).getOrNull() ?: return@async null
                            val rewardsInfo = (rewardsInfoResult as Result<ZeroStakingUserRewardsInfo>).getOrNull() ?: return@async null
                            val stakeToken = (stakeTokenResult as Result<ZeroTokenAddress>).getOrNull() ?: return@async null

                            val tokenPrice = if (WalletChainsUtil.isAvaxChain(chainId)) {
                                client.getAvaxTokenPrice(stakeToken.address).getOrNull()?.usd
                            } else {
                                price.price
                            }

                            val pool = HomeStakePool.from(
                                userAddress = userAddress,
                                pool = stakePool,
                                tokenPrice = tokenPrice,
                                totalStakedAmount = totalStaked,
                                stakingStatus = stakeStatus,
                                rewardsInfo = rewardsInfo
                            )

                            if (refreshAllData) onRefreshAllData(pool)

                            pool
                        } else null
                    }
                }.awaitAll().filterNotNull()

                stakePoolsContent.value = pools
                    .distinctBy { it.poolAddress }
                    .sortedBy { it.chainId }
            }
        }
    }

    private fun CoroutineScope.fetchPoolData(
        pool: HomeStakePool,
        selectedStakePoolState: MutableState<SelectedStakePool?>,
        genericActionState: MutableState<AsyncAction<Unit>>,
        showWalletStakingSheetState: MutableState<Boolean>,
        isSilentRefresh: Boolean = false
    ) = launch {
        genericActionState.value = AsyncAction.Uninitialized
        if (!isSilentRefresh) {
            genericActionState.value = AsyncAction.Loading
        }

        val chainId = pool.chainId
        val fetchTokenData: suspend (String) -> Pair<ZeroWalletTokenInfo?, ZeroWalletTokenBalance?> = { tokenAddress ->
            val results = awaitAll(
                async { client.getTokenInfo(tokenAddress, chainId) },
                async { client.getTokenBalance(pool.userWalletAddress, tokenAddress, chainId) },
            )
            val tokenInfoResult = (results[0] as? Result<ZeroWalletTokenInfo>)?.getOrNull()
            val tokenBalanceResult = (results[1] as? Result<ZeroWalletTokenBalance>)?.getOrNull()
            (tokenInfoResult to tokenBalanceResult)
        }
        val tokensResult = awaitAll(
            async { client.getStakingToken(pool.poolAddress, chainId) },
            async { client.getRewardToken(pool.poolAddress, chainId) },
        )
        var stakeTokenInfo: ZeroWalletTokenInfo? = null
        var stakeTokenBalance: ZeroWalletTokenBalance? = null
        var rewardsTokenInfo: ZeroWalletTokenInfo? = null
        var rewardsTokenBalance: ZeroWalletTokenBalance? = null
        (tokensResult[0] as? Result<ZeroTokenAddress>)?.getOrNull()?.let {
            val (tokenInfo, tokenBalance) = fetchTokenData(it.address)
            stakeTokenInfo = tokenInfo
            stakeTokenBalance = tokenBalance
        }
        (tokensResult[1] as? Result<ZeroTokenAddress>)?.getOrNull()?.let {
            val (tokenInfo, tokenBalance) = fetchTokenData(it.address)
            rewardsTokenInfo = tokenInfo
            rewardsTokenBalance = tokenBalance
        }
        if (stakeTokenInfo != null && stakeTokenBalance != null && rewardsTokenInfo != null && rewardsTokenBalance != null) {
            genericActionState.value = AsyncAction.Success(Unit)
            val selectedStakePoolId = selectedStakePoolState.value?.poolInfo?.poolAddress
            if (selectedStakePoolId != null) {
                if (pool.poolAddress == selectedStakePoolId) {
                    selectedStakePoolState.value = SelectedStakePool(
                        poolInfo = pool,
                        stakeTokenInfo = stakeTokenInfo,
                        stakeTokenBalance = stakeTokenBalance,
                        rewardsTokenInfo = rewardsTokenInfo,
                        rewardsTokenBalance = rewardsTokenBalance
                    )
                }
            } else {
                selectedStakePoolState.value = SelectedStakePool(
                    poolInfo = pool,
                    stakeTokenInfo = stakeTokenInfo,
                    stakeTokenBalance = stakeTokenBalance,
                    rewardsTokenInfo = rewardsTokenInfo,
                    rewardsTokenBalance = rewardsTokenBalance
                )
                showWalletStakingSheetState.value = true
            }
        } else {
            genericActionState.value = AsyncAction.Failure(Throwable("Failed to fetch pool data. Required values are missing"))
        }
    }

    private fun CoroutineScope.claimStakingRewards(
        pool: HomeStakePool,
        genericActionState: MutableState<AsyncAction<Unit>>,
        onDone: () -> Unit,
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.claimStakingRewards(
            userAddress = pool.userWalletAddress,
            poolAddress = pool.poolAddress,
            chainId = pool.chainId
        ).onSuccess {
            genericActionState.value = AsyncAction.Success(Unit)
            onDone()
        }.onFailure {
            genericActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun CoroutineScope.stakeAmount(
        stakePool: SelectedStakePool,
        amount: String,
        walletStakeActionState: MutableState<AsyncAction<String>>,
        onSuccess: () -> Unit,
    ) = launch {
        walletStakeActionState.value = AsyncAction.Loading
        val transactionAmount = amount.toLocalizedDoubleOrZero()
        client.stakeAmount(
            userAddress = stakePool.poolInfo.userWalletAddress,
            amount = toSmallestUnit(transactionAmount, 18),
            poolAddress = stakePool.poolInfo.poolAddress,
            tokenAddress = stakePool.stakeTokenInfo.address,
            chainId = stakePool.poolInfo.chainId
        ).onSuccess {
            walletStakeActionState.value = AsyncAction.Success(it)
            onSuccess.invoke()
        }.onFailure {
            walletStakeActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun CoroutineScope.unstakeAmount(
        stakePool: SelectedStakePool,
        amount: String,
        walletStakeActionState: MutableState<AsyncAction<String>>,
        onSuccess: () -> Unit
    ) = launch {
        walletStakeActionState.value = AsyncAction.Loading
        val transactionAmount = amount.toLocalizedDoubleOrZero()
        client.unstakeAmount(
            userAddress = stakePool.poolInfo.userWalletAddress,
            amount = toSmallestUnit(transactionAmount, 18),
            poolAddress = stakePool.poolInfo.poolAddress,
            chainId = stakePool.poolInfo.chainId
        ).onSuccess {
            walletStakeActionState.value = AsyncAction.Success(it)
            onSuccess.invoke()
        }.onFailure {
            walletStakeActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun toSmallestUnit(amount: Double, decimals: Int): String {
        val decimalValue = BigDecimal.valueOf(amount)
            .multiply(BigDecimal.TEN.pow(decimals))
            .setScale(0, RoundingMode.DOWN)
        return decimalValue.toBigInteger().toString()
    }
}

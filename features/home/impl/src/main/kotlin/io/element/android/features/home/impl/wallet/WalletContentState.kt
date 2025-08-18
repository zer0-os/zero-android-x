/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.features.home.impl.model.SelectedStakePool
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class WalletContentState(
    val userName: String,
    val showWalletBalance: Boolean,
    private val walletBalance: Double,
    val walletTransactionUrlState: AsyncAction<String>,

    val claimableRewards: ZeroUserRewards,

    val tokensListState: WalletTokensListState,
    val transactionsListState: WalletTransactionsListState,
    val tokensPaginationParams: ZeroWalletTokensPaginationParams?,
    val transactionsPaginationParams: ZeroWalletTransactionsPaginationParams?,
    val meowPrice: ZeroMeowPrice?,

    val stakePools: List<HomeStakePool>,
    val selectedPool: SelectedStakePool?,
    val showStakingSheet: Boolean,
    val walletStakeActionState: AsyncAction<String> = AsyncAction.Uninitialized,

    val eventSink: (HomeEvents.HomeWalletEvents) -> Unit,
) {
    val userWalletBalance: String
        get() = if (showWalletBalance) {
            "$${walletBalance}"
        } else "****"
}

@Immutable
sealed interface WalletTokensListState {
    data class Skeleton(val count: Int) : WalletTokensListState
    data object Empty : WalletTokensListState
    data class Tokens(
        val tokens: ImmutableList<ZeroWalletToken>
    ) : WalletTokensListState
}

@Immutable
sealed interface WalletTransactionsListState {
    data class Skeleton(val count: Int) : WalletTransactionsListState
    data object Empty : WalletTransactionsListState
    data class Transactions(
        val transactions: ImmutableList<ZeroWalletTransaction>
    ) : WalletTransactionsListState
}

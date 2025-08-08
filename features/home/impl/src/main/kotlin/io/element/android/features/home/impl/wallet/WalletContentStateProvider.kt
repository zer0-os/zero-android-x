/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

open class WalletContentStateProvider : PreviewParameterProvider<WalletContentState> {
    override val values: Sequence<WalletContentState>
        get() = sequenceOf(
            aWalletContentState(showWalletBalance = true),
            aWalletContentState(showWalletBalance = false),
            aWalletContentState(tokensListState = WalletTokensListState.Skeleton(10)),
            aWalletContentState(transactionsListState = WalletTransactionsListState.Skeleton(10)),
        )
}

internal fun aWalletContentState(
    showWalletBalance: Boolean = true,
    tokensListState: WalletTokensListState = WalletTokensListState.Empty,
    transactionsListState: WalletTransactionsListState = WalletTransactionsListState.Empty,
) = WalletContentState(
    userName = "Jade David",
    showWalletBalance = showWalletBalance,
    walletBalance = 0.0,
    walletTransactionUrlState = AsyncAction.Uninitialized,
    claimableRewards = ZeroUserRewards.empty(),
    tokensListState = tokensListState,
    transactionsListState = transactionsListState,
    tokensPaginationParams = null,
    transactionsPaginationParams = null,
    meowPrice = null,
    eventSink = {}
)

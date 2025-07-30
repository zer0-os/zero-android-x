/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

open class WalletContentStateProvider : PreviewParameterProvider<WalletContentState> {
    override val values: Sequence<WalletContentState>
        get() = sequenceOf(
            aWalletContentState()
        )
}

internal fun aWalletContentState() = WalletContentState(
    userName = "Jade David",
    showWalletBalance = true,
    walletBalance = 0.0,
    tokensListState = WalletTokensListState.Empty,
    transactionsListState = WalletTransactionsListState.Empty,
    tokensPaginationParams = null,
    transactionsPaginationParams = null,
    eventSink = {}
)

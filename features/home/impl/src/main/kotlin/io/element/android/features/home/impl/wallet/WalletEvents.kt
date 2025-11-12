/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction

sealed interface WalletEvents {
    data class LoadMoreTokens(val currentTokens: List<ZeroWalletToken>) : WalletEvents
    data class LoadMoreTransactions(val currentTransactions: List<ZeroWalletTransaction>) : WalletEvents
    data class ViewWalletTransaction(val transactionId: String, val chainId: Long? = null) : WalletEvents
    data object OnWalletTransactionViewed : WalletEvents
    data object ToggleWalletBalance : WalletEvents
    data object RefreshWalletBalance : WalletEvents
    data class StakePoolSelected(val pool: HomeStakePool) : WalletEvents
    data class StakeAmount(val amount: String) : WalletEvents
    data class UnstakeAmount(val amount: String) : WalletEvents
    data object DismissStakingSheet : WalletEvents
    data object ClaimStakingRewards : WalletEvents
    data object RefreshWallet : WalletEvents
}

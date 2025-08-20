/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenBalance
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.data.model.helper.RewardsUtil

@Immutable
data class SelectedStakePool(
    val poolInfo: HomeStakePool,
    val stakeTokenInfo: ZeroWalletTokenInfo,
    val stakeTokenBalance: ZeroWalletTokenBalance,
    val rewardsTokenInfo: ZeroWalletTokenInfo,
    val rewardsTokenBalance: ZeroWalletTokenBalance
) {
    val myStakedTokens: Double
        get() = RewardsUtil.parseCredits(
            poolInfo.tokenAmount,
            stakeTokenInfo.decimals
        )

    val myStakedTokensFormatted: String
        get() = ZeroWalletUtil.getFormattedNumber(myStakedTokens)

    val totalAvailableTokenBalance: Double
        get() = RewardsUtil.parseCredits(
            stakeTokenBalance.balance,
            stakeTokenInfo.decimals
        )

    val totalAvailableTokenBalanceFormatted: String
        get() = ZeroWalletUtil.getFormattedNumber(totalAvailableTokenBalance)
}

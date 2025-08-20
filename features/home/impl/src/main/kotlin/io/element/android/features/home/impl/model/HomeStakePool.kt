/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingConfig
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingStatus
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUserRewardsInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.config.ZeroConfig
import io.element.android.support.zero.data.model.helper.RewardsUtil

@Immutable
data class HomeStakePool(
    val userWalletAddress: String,
    val poolAddress: String,
    val poolIcon: String?,
    val poolDisplayName: String,
    val tokenAmount: String,
    val tokenIcon: String?,
    val totalStakedAmount: Double,
    val myStakeAmount: Double,
    val pendingRewards: Double,
) {
    val totalStakedAmountFormatted: String
        get() = ZeroWalletUtil.getFormattedNumber(totalStakedAmount)
    val myStakeAmountFormatted: String
        get() = ZeroWalletUtil.getFormattedNumber(myStakeAmount)

    companion object {
        fun from(
            userAddress: String, poolAddress: String, meowPrice: ZeroMeowPrice,
            totalStakedAmount: String, stakingConfig: ZeroStakingConfig,
            stakingStatus: ZeroStakingStatus, rewardsInfo: ZeroStakingUserRewardsInfo
        ): HomeStakePool {
            val totalStakedAmount = RewardsUtil.parseCredits(totalStakedAmount, 18)
            val totalStakedAmountRefPrice = ZeroWalletUtil.getBalance(totalStakedAmount, meowPrice)
            val myStakedAmount = RewardsUtil.parseCredits(stakingStatus.amountStaked, 18)
            val myStakedAmountRefPrice = ZeroWalletUtil.getBalance(myStakedAmount, meowPrice)
            val pendingRewards = RewardsUtil.parseCredits(rewardsInfo.pendingRewards, 18)
            return HomeStakePool(
                userWalletAddress = userAddress,
                poolAddress = poolAddress,
                poolIcon = ZeroConfig.MEOW_ICON_URL,
                poolDisplayName = ZeroConfig.MEOW_POOL_NAME,
                tokenAmount = stakingStatus.amountStaked,
                tokenIcon = ZeroConfig.MEOW_ICON_URL,
                totalStakedAmount = totalStakedAmountRefPrice,
                myStakeAmount = myStakedAmountRefPrice,
                pendingRewards = pendingRewards
            )
        }
    }
}

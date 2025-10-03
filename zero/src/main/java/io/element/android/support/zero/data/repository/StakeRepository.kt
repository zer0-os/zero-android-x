/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.staking.ApiStakingConfig
import io.element.android.support.zero.network.model.response.staking.ApiStakingStatus
import io.element.android.support.zero.network.model.response.staking.ApiStakingUserRewardsInfo
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingRewardsToken
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingToken
import io.element.android.support.zero.network.model.response.wallet.ApiTransactionPerformed

interface StakeRepository {

    suspend fun getTotalStaked(poolAddress: String, chainId: Long): String

    suspend fun getStakingConfig(poolAddress: String, chainId: Long): ApiStakingConfig

    suspend fun getStakerStatusInfo(userAddress: String, poolAddress: String, chainId: Long): ApiStakingStatus

    suspend fun getStakeRewardsInfo(userAddress: String, poolAddress: String, chainId: Long): ApiStakingUserRewardsInfo

    suspend fun getStakingToken(poolAddress: String, chainId: Long): ApiWalletStakingToken

    suspend fun getRewardToken(poolAddress: String, chainId: Long): ApiWalletStakingRewardsToken

    suspend fun stakeAmount(userAddress: String, amount: String, poolAddress: String, chainId: Long): ApiTransactionPerformed

    suspend fun unstakeAmount(userAddress: String, amount: String, poolAddress: String, chainId: Long): ApiTransactionPerformed

    suspend fun claimStakingRewards(userAddress: String, poolAddress: String, chainId: Long): ApiTransactionPerformed
}

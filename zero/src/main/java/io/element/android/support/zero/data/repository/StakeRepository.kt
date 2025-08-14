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

    suspend fun getTotalStaked(poolAddress: String): String

    suspend fun getStakingConfig(poolAddress: String): ApiStakingConfig

    suspend fun getStakerStatusInfo(userAddress: String, poolAddress: String): ApiStakingStatus

    suspend fun getStakeRewardsInfo(userAddress: String, poolAddress: String): ApiStakingUserRewardsInfo

    suspend fun getStakingToken(poolAddress: String): ApiWalletStakingToken

    suspend fun getRewardToken(poolAddress: String): ApiWalletStakingRewardsToken

    suspend fun stakeAmount(userAddress: String, amount: String, poolAddress: String): ApiTransactionPerformed

    suspend fun unstakeAmount(userAddress: String, amount: String, poolAddress: String): ApiTransactionPerformed
}

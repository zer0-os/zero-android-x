/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.ClaimStakingRewardsRequest
import io.element.android.support.zero.network.model.request.StakeTransactionRequest
import io.element.android.support.zero.network.model.request.UnstakeTransactionRequest
import io.element.android.support.zero.network.model.response.staking.ApiStakingConfig
import io.element.android.support.zero.network.model.response.staking.ApiStakingStatus
import io.element.android.support.zero.network.model.response.staking.ApiStakingUserRewardsInfo
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingRewardsToken
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingToken
import io.element.android.support.zero.network.model.response.wallet.ApiTransactionPerformed
import io.element.android.support.zero.network.service.ZeroStakeService

class StakeRepositoryImpl(
    private val zeroStakeService: ZeroStakeService
) : StakeRepository {
    override suspend fun getTotalStaked(poolAddress: String, chainId: Int): String {
        return zeroStakeService.getTotalStaked(poolAddress, chainId.toString())
    }

    override suspend fun getStakingConfig(poolAddress: String, chainId: Int): ApiStakingConfig {
        return zeroStakeService.getStakingConfig(poolAddress, chainId.toString())
    }

    override suspend fun getStakerStatusInfo(userAddress: String, poolAddress: String, chainId: Int): ApiStakingStatus {
        return zeroStakeService.getStakerStatusInfo(userAddress, poolAddress, chainId.toString())
    }

    override suspend fun getStakeRewardsInfo(userAddress: String, poolAddress: String, chainId: Int): ApiStakingUserRewardsInfo {
        return zeroStakeService.getStakeRewardsInfo(userAddress, poolAddress, chainId.toString())
    }

    override suspend fun getStakingToken(poolAddress: String, chainId: Int): ApiWalletStakingToken {
        return zeroStakeService.getStakingToken(poolAddress, chainId.toString())
    }

    override suspend fun getRewardToken(poolAddress: String, chainId: Int): ApiWalletStakingRewardsToken {
        return zeroStakeService.getRewardToken(poolAddress, chainId.toString())
    }

    override suspend fun stakeAmount(userAddress: String, amount: String, poolAddress: String, chainId: Int): ApiTransactionPerformed {
        val request = StakeTransactionRequest(amount, poolAddress, chainId)
        return zeroStakeService.stakeAmount(userAddress, request)
    }

    override suspend fun unstakeAmount(userAddress: String, amount: String, poolAddress: String, chainId: Int): ApiTransactionPerformed {
        val request = UnstakeTransactionRequest(amount, poolAddress, chainId)
        return zeroStakeService.unstakeAmount(userAddress, request)
    }

    override suspend fun claimStakingRewards(userAddress: String, poolAddress: String, chainId: Int): ApiTransactionPerformed {
        val request = ClaimStakingRewardsRequest(poolAddress, chainId)
        return zeroStakeService.claimStakingRewards(userAddress, request)
    }
}

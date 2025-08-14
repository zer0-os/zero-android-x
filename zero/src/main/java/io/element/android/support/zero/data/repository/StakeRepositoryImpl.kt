/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

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
    override suspend fun getTotalStaked(poolAddress: String): String {
        return zeroStakeService.getTotalStaked(poolAddress)
    }

    override suspend fun getStakingConfig(poolAddress: String): ApiStakingConfig {
        return zeroStakeService.getStakingConfig(poolAddress)
    }

    override suspend fun getStakerStatusInfo(userAddress: String, poolAddress: String): ApiStakingStatus {
        return zeroStakeService.getStakerStatusInfo(userAddress, poolAddress)
    }

    override suspend fun getStakeRewardsInfo(userAddress: String, poolAddress: String): ApiStakingUserRewardsInfo {
        return zeroStakeService.getStakeRewardsInfo(userAddress, poolAddress)
    }

    override suspend fun getStakingToken(poolAddress: String): ApiWalletStakingToken {
        return zeroStakeService.getStakingToken(poolAddress)
    }

    override suspend fun getRewardToken(poolAddress: String): ApiWalletStakingRewardsToken {
        return zeroStakeService.getRewardToken(poolAddress)
    }

    override suspend fun stakeAmount(userAddress: String, amount: String, poolAddress: String): ApiTransactionPerformed {
        val request = StakeTransactionRequest(amount, poolAddress)
        return zeroStakeService.stakeAmount(userAddress, request)
    }

    override suspend fun unstakeAmount(userAddress: String, amount: String, poolAddress: String): ApiTransactionPerformed {
        val request = UnstakeTransactionRequest(amount, poolAddress)
        return zeroStakeService.unstakeAmount(userAddress, request)
    }
}

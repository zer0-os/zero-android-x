/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.ClaimStakingRewardsRequest
import io.element.android.support.zero.network.model.request.StakeTransactionRequest
import io.element.android.support.zero.network.model.request.UnstakeTransactionRequest
import io.element.android.support.zero.network.model.response.staking.ApiStakingConfig
import io.element.android.support.zero.network.model.response.staking.ApiStakingStatus
import io.element.android.support.zero.network.model.response.staking.ApiStakingUserRewardsInfo
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingRewardsToken
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingToken
import io.element.android.support.zero.network.model.response.wallet.ApiTransactionPerformed
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZeroStakeService {

    @GET(value = "api/staking/{pool_address}/total-staked")
    suspend fun getTotalStaked(
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): String

    @GET(value = "api/staking/{pool_address}/config")
    suspend fun getStakingConfig(
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): ApiStakingConfig

    @GET(value = "api/staking/{user_address}/stakers/{pool_address}")
    suspend fun getStakerStatusInfo(
        @Path("user_address") userAddress: String,
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): ApiStakingStatus

    @GET(value = "api/staking/{user_address}/rewards/{pool_address}")
    suspend fun getStakeRewardsInfo(
        @Path("user_address") userAddress: String,
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): ApiStakingUserRewardsInfo

    @GET(value = "api/staking/{pool_address}/staking-token")
    suspend fun getStakingToken(
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): ApiWalletStakingToken

    @GET(value = "api/staking/{pool_address}/rewards-token")
    suspend fun getRewardToken(
        @Path("pool_address") poolAddress: String,
        @Query("chainId") chainId: String,
    ): ApiWalletStakingRewardsToken

    @POST(value = "api/wallet/{user_address}/transactions/stake")
    suspend fun stakeAmount(
        @Path("user_address") userAddress: String,
        @Body request: StakeTransactionRequest
    ): ApiTransactionPerformed

    @POST(value = "api/wallet/{user_address}/transactions/unstake")
    suspend fun unstakeAmount(
        @Path("user_address") userAddress: String,
        @Body request: UnstakeTransactionRequest
    ): ApiTransactionPerformed

    @POST(value = "api/wallet/{user_address}/transactions/claim-staking-rewards")
    suspend fun claimStakingRewards(
        @Path("user_address") userAddress: String,
        @Body request: ClaimStakingRewardsRequest
    ): ApiTransactionPerformed
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingConfig
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingStatus
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUserRewardsInfo
import io.element.android.libraries.matrix.api.zero.staking.ZeroTokenAddress
import io.element.android.support.zero.network.model.response.staking.ApiStakingConfig
import io.element.android.support.zero.network.model.response.staking.ApiStakingStatus
import io.element.android.support.zero.network.model.response.staking.ApiStakingUserRewardsInfo
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingRewardsToken
import io.element.android.support.zero.network.model.response.staking.ApiWalletStakingToken

fun ApiStakingConfig.toModel() = ZeroStakingConfig(
    timestamp = timestamp,
    rewardsPerPeriod = rewardsPerPeriod,
    periodLength = periodLength,
    minimumLockTime = minimumLockTime,
    minimumRewardsMultiplier = minimumRewardsMultiplier,
    maximumRewardsMultiplier = maximumRewardsMultiplier,
    canExit = canExit
)

fun ApiStakingStatus.toModel() = ZeroStakingStatus(
    unlockedTimestamp = unlockedTimestamp,
    amountStaked = amountStaked,
    amountStakedLocked = amountStakedLocked,
    owedRewards = owedRewards,
    owedRewardsLocked = owedRewardsLocked,
    lastTimestamp = lastTimestamp,
    lastTimestampLocked = lastTimestamp
)

fun ApiStakingUserRewardsInfo.toModel() = ZeroStakingUserRewardsInfo(
    pendingRewards = pendingRewards,
    poolAddress = poolAddress,
    userAddress = userAddress
)

fun ApiWalletStakingToken.toModel() = ZeroTokenAddress(stakingTokenAddress)

fun ApiWalletStakingRewardsToken.toModel() = ZeroTokenAddress(rewardsTokenAddress)

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.staking

data class ZeroStakingUserRewardsInfo(
    val pendingRewards: String,
    val poolAddress: String,
    val userAddress: String
)

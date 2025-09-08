/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ClaimStakingRewardsRequest(
    val poolAddress: String,
    val chainId: Int
)

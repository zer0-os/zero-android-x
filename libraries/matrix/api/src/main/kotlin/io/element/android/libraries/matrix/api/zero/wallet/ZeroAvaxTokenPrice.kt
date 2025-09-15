/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

data class ZeroAvaxTokenPrice(
    val usd: Double,
    val marketCap: Double,
    val volume24h: Double,
    val change24h: Double,
    val lastUpdatedAt: Int
)

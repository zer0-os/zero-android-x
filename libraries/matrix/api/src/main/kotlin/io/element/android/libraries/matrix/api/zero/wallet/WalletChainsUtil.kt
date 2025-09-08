/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import androidx.annotation.DrawableRes

object WalletChainsUtil {
    const val Z_CHAIN_ID: Int = 9369
    const val Z_CHAIN_ID_ZEPHYR: Int = 9369
    const val AVAX_CHAIN_ID: Int = 43114

    fun isZChain(chainId: Int) = chainId == Z_CHAIN_ID
    fun isAvaxChain(chainId: Int) = chainId == AVAX_CHAIN_ID
}

data class WalletChain(
    val id: Int,
    val name: String,
    @DrawableRes val logo: Int
)

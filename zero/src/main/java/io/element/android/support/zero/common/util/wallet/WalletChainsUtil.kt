/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.util.wallet

import androidx.annotation.DrawableRes
import io.element.android.support.zero.R

object WalletChainsUtil {
    val z_chain = ZeroChain.Z
    val avax_chain = ZeroChain.AVAX

    fun isZChain(chainId: Long): Boolean {
        return chainId == z_chain.chainId
    }

    fun isAvaxChain(chainId: Long): Boolean {
        return chainId == avax_chain.chainId
    }

    fun getChain(chainId: Long): ZeroChain? {
        return ZeroChain.fromId(chainId)
    }
}

enum class ZeroChain(
    val chainId: Long,
    val chainName: String,
    @DrawableRes val logo: Int
) {
    // ZChains
    Z(9369, "Z", R.drawable.ic_chain_z),
    Z_ZEPHYR(1417429182, "Z_Zephyr", R.drawable.ic_chain_z),

    // Ethereum Chains
    ETHEREUM(1, "Ethereum", R.drawable.ic_chain_ethereum),
    ETHEREUM_SEPOLIA(11155111, "Ethereum_Sepolia", R.drawable.ic_chain_ethereum),

    // Avax Chains
    AVAX(43114, "Avax", R.drawable.ic_chain_avax),
    AVAX_FUJI(43113, "Avax_Fuji", R.drawable.ic_chain_avax),

    // Polygon Chains
    POLYGON(137, "Polygon", R.drawable.ic_chain_polygon),
    POLYGON_AMOY(80002, "Polygon_Amoy", R.drawable.ic_chain_polygon),

    // Base Chains
    BASE(8453, "Base", R.drawable.ic_chain_base),
    BASE_SEPOLIA(84532, "Base_Sepolia", R.drawable.ic_chain_base);

    companion object {
        fun fromId(id: Long): ZeroChain? {
            return entries.find { it.chainId == id }
        }
    }
}

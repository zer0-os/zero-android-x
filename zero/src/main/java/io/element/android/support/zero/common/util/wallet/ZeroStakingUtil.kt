/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.util.wallet

object ZeroStakingUtil {
    // Meow Pool
    private val meowPool: WalletStakePool by lazy {
        WalletStakePool(
            id = "0xfbDC0647F0652dB9eC56c7f09B7dD3192324AD6a",
            address = "0xfbDC0647F0652dB9eC56c7f09B7dD3192324AD6a",
            name = "MEOW Pool",
            image = "https://zos.zero.tech/tokens/meow.png",
            chainId = WalletChainsUtil.z_chain.chainId
        )
    }

    // Meow Avax Pool
    private val meowAvaxPool: WalletStakePool by lazy {
        WalletStakePool(
            id = "0xD7A1583286cEB8ce8F3C1a6d50C5eBDB1Cd83358",
            address = "0xD7A1583286cEB8ce8F3C1a6d50C5eBDB1Cd83358",
            name = "MEOW Pool",
            image = "https://zos.zero.tech/tokens/meow-avax.png",
            chainId = WalletChainsUtil.avax_chain.chainId
        )
    }

    val stakePools = listOf(meowPool, meowAvaxPool)
}

data class WalletStakePool(
    val id: String,
    val address: String,
    val name: String,
    val image: String?,
    val chainId: Long
)

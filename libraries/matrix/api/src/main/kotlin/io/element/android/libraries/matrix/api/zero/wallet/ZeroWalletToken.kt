/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import android.os.Parcelable
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroWalletTokensResponse(
    val tokens: List<ZeroWalletToken>,
    val paginationParams: ZeroWalletTokensPaginationParams?
): Parcelable

@Parcelize
data class ZeroWalletToken(
    val tokenAddress: String,
    val symbol: String,
    val name: String,
    val amount: String,
    val logo: String?,
    val decimals: Int,
    val chainId: Long,
    val percentChange: String?,
    val price: Double?
): Parcelable

val ZeroWalletToken.isMeowToken: Boolean
    get() = symbol.equals("Meow", true)

val ZeroWalletToken.isVMeowToken: Boolean
    get() = symbol.equals("vMeow", true)

val ZeroWalletToken.isClaimableToken: Boolean
    get() = isVMeowToken || isMeowToken

val ZeroWalletToken.tokenAmount: Double
    get() = amount.toDoubleOrNull() ?: 0.0

val ZeroWalletToken.tokenPrice: Double
    get() = ZeroWalletUtil.getTokenPrice(tokenAmount, this.price)

val ZeroWalletToken.tokenPriceFormatted: String
    get() = ZeroWalletUtil.getTokenPriceFormatted(tokenAmount, this.price)

fun ZeroWalletToken.meowPrice(meowPrice: ZeroMeowPrice): Double {
    return ZeroWalletUtil.getBalance(tokenAmount, meowPrice.price)
}

fun ZeroWalletToken.meowPriceFormatted(meowPrice: ZeroMeowPrice): String {
    return ZeroWalletUtil.getBalanceFormatted(tokenAmount, meowPrice)
}

@Parcelize
data class ZeroWalletTokensPaginationParams(
    val itemsCount: Int,
    val tokenName: String?,
    val tokenType: String?,
    val value: Int
): Parcelable

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import android.annotation.SuppressLint
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.pow

object ZeroWalletUtil {

    fun walletAddressDisplayText(walletAddress: String?): String? {
        return if (walletAddress.isNullOrBlank()) {
            null
        } else {
            buildString {
                append(walletAddress.take(6))
                append("....")
                append(walletAddress.takeLast(4))
            }
        }
    }

    fun getBalance(meowTokenAmount: Double, meowPrice: Double?): Double {
        return meowTokenAmount.times(meowPrice ?: 0.0)
            .toBigDecimal()
            .setScale(2, RoundingMode.UP)
            .toDouble()
    }

    fun getBalanceFormatted(tokenAmount: Double, meowPrice: ZeroMeowPrice): String {
        val amount = getBalance(tokenAmount, meowPrice.price)
        return getFormattedNumber(amount, withAbbreviations = false)
    }

    fun getTokenPrice(tokenAmount: Double, tokenPrice: Double?): Double {
        return tokenAmount.times(tokenPrice ?: 0.0)
            .toBigDecimal()
            .setScale(2, RoundingMode.UP)
            .toDouble()
    }

    fun getTokenPriceFormatted(tokenAmount: Double, tokenPrice: Double?): String {
        val amount = getTokenPrice(tokenAmount, tokenPrice)
        return getFormattedNumber(amount, withAbbreviations = false)
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedNumber(count: Double, withAbbreviations: Boolean = true): String {
        if (count.isNaN() || count.isInfinite()) return "0" // handle weird inputs
        return if (withAbbreviations) {
            if (count < 1000.0) {
                // Small numbers, just format without suffix
                String.format("%.2f", count)
            } else {
                val exp = (ln(count) / ln(1000.0)).toInt().coerceAtLeast(1)
                val value = count / 1000.0.pow(exp.toDouble())
                val suffixes = "KMBTPE"
                val suffix = if (exp - 1 in suffixes.indices) suffixes[exp - 1] else ' '
                String.format("%.2f%c", value, suffix)
            }
        } else {
            String.format("%.2f", count)
        }
    }

    fun thousandSeparatedFormat(credits: String): String {
        val splitCredits = credits.split(".")
        return if (splitCredits.size > 1) {
            val pre = (splitCredits[0].toIntOrNull() ?: 0).toThousandSpacedString()
            buildString {
                append(pre)
                append(".")
                append(splitCredits[1])
            }
        } else {
            credits
        }
    }
}

@SuppressLint("DefaultLocale")
private fun Int.toThousandSpacedString(): String {
    return try {
        when {
            this in 1..999 -> String.format("%d", this)
            this > 999 -> {
                (this / 1000).toThousandSpacedString() + String.format(",%03d", this % 1000)
            }
            else -> "0"
        }
    } catch (e: Exception) {
        String.format("%d", this)
    }
}

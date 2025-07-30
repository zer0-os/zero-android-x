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

    fun getWalletBalance(meowTokenAmount: Double, meowPrice: ZeroMeowPrice): Double {
        return meowTokenAmount.times(meowPrice.price ?: 0.0)
            .toBigDecimal()
            .setScale(2, RoundingMode.UP)
            .toDouble()
    }

    fun getMeowTokenPriceFormatted(tokenAmount: Double, meowPrice: ZeroMeowPrice): String {
        val amount = tokenAmount.times(meowPrice.price ?: 0.0)
            .toBigDecimal()
            .setScale(2, RoundingMode.UP)
            .toDouble()
        return getFormattedNumber(amount, withAbbreviations = false)
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedNumber(count: Double, withAbbreviations: Boolean = true): String {
        if (count < 1000) return "" + count
        val exp = (ln(count) / ln(1000.0)).toInt()
        return if (withAbbreviations) {
            String.format("%.2f%c", count / 1000.0.pow(exp.toDouble()), "KMBTPE"[exp - 1])
        } else {
            String.format("%.2f%c", count / 1000.0.pow(exp.toDouble()))
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

package io.element.android.support.zero.data.model.helper

import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.data.model.UserRewards
import java.math.RoundingMode

object RewardsUtil {

    fun parseZeroCredits(userRewards: UserRewards): Double {
        return parseCredits(userRewards.zero, userRewards.decimals)
    }

    fun getEarnedRewards(zero: String, decimals: Int): Float {
        val current = parseRewards(zero, decimals).toFloatOrNull() ?: 0f
        return current
    }

    fun getEarnedRewardsFormatted(zero: String, decimals: Int): String {
        val rewardEarned = getEarnedRewards(zero, decimals)
        return ZeroWalletUtil.thousandSeparatedFormat(rewardEarned.toString())
    }

    fun getRefPrice(zero: String, decimals: Int, refPrice: Double): String {
        return try {
            val credits = parseCredits(zero, decimals)
            if (refPrice > 0) {
                val price = credits.times(refPrice).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                if (price > 0) {
                    ZeroWalletUtil.getFormattedNumber(price)
                } else {
                    "0"
                }
            } else {
                "0"
            }
        } catch (e: Exception) {
            "0"
        }
    }

    fun parseCredits(credits: String, decimals: Int): Double {
        return try {
            if (credits.isBlank()) return 0.0
            val delimiter = (credits.length - decimals).coerceAtLeast(1) // prevent negative or 0
            val intPart = credits.substring(0, delimiter)
            val result = intPart.toDoubleOrNull()
            result ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun parseRewards(zero: String, decimals: Int): String =
        try {
            val delimiter = zero.length - decimals
            buildString {
                append(zero.substring(0, delimiter))
                append(".")
                append(zero[delimiter])
            }
        } catch (e: Exception) {
            zero
        }
}

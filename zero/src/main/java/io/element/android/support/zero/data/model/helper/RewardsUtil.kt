package io.element.android.support.zero.data.model.helper

import android.annotation.SuppressLint
import io.element.android.support.zero.common.extension.toThousandSpacedString
import io.element.android.support.zero.data.model.UserRewards
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.pow

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
        return thousandSeparatedFormat(rewardEarned.toString())
    }

    fun getRefPrice(zero: String, decimals: Int, refPrice: Double): String {
        return try {
            val credits = parseCredits(zero, decimals)
            if (refPrice > 0) {
                val price = credits.times(refPrice).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                getFormattedNumber(price)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseCredits(credits: String, decimals: Int): Double {
        return try {
            val delimiter = credits.length - decimals
            credits.substring(0, delimiter).toDoubleOrNull() ?: 0.0
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

    @SuppressLint("DefaultLocale")
    private fun getFormattedNumber(count: Double): String {
        if (count < 1000) return "" + count
        val exp = (ln(count) / ln(1000.0)).toInt()
        return String.format("%.2f%c", count / 1000.0.pow(exp.toDouble()), "KMBTPE"[exp - 1])
    }

    private fun thousandSeparatedFormat(credits: String): String {
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

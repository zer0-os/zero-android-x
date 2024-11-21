package io.element.android.support.zero.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRewards(
	val zero: String,
	val zeroPreviousDay: String,
	val decimals: Int,
	val price: Double = 0.0,
	val reference: String = ""
) {
	var earnedRewards: Double = 0.0

    companion object {
        fun empty() = UserRewards(zero = "0", zeroPreviousDay = "0", decimals = 0)
    }
}

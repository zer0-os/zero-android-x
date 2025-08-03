package io.element.android.libraries.matrix.api.zero.rewards

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroUserRewards(
    val zero: String,
    val decimals: Int,
    val price: Double = 0.0,
    val unclaimedRewards: String,
    val hasUnclaimedRewards: Boolean
): Parcelable {

    companion object {
        fun empty() = ZeroUserRewards(zero = "", decimals = 0, unclaimedRewards = "", hasUnclaimedRewards = false)
    }
}

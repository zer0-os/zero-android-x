package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiUserRewards(
    val zero: String,
    val zeroPreviousDay: String,
    val decimals: Int,

    val legacyRewards: String,
    val meow: String,
    val meowPreviousDay: String,
    val totalDailyRewards: String,
    val totalReferralFees: String,
    val unclaimedRewards: String
)

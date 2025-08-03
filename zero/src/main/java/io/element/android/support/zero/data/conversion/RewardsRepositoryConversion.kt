package io.element.android.support.zero.data.conversion

import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiUserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens
import okhttp3.internal.toLongOrDefault

internal fun ApiUserRewards.toModel(zeroTokens: ApiZeroTokens?): UserRewards {
    val legacyRewards = legacyRewards
    val dailyRewards = totalDailyRewards
    val referralRewards = totalReferralFees
    val unClaimedRewards = unclaimedRewards.toLongOrDefault(0)
    val combinedRewards = legacyRewards.toLongOrDefault(0)
        .plus(dailyRewards.toLongOrDefault(0))
        .plus(referralRewards.toLongOrDefault(0))

    return UserRewards(
        zero = combinedRewards.toString(),
        zeroPreviousDay = zeroPreviousDay,
        unclaimedRewards = unclaimedRewards,
        decimals = decimals,
        price = zeroTokens?.price ?: 0.0,
        reference = zeroTokens?.reference ?: "",
        hasUnclaimedRewards = unClaimedRewards > 0
    )
}

package io.element.android.support.zero.data.conversion

import io.element.android.support.zero.common.extension.isPositive
import io.element.android.support.zero.common.extension.toBigDecimalOrZero
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiUserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens

internal fun ApiUserRewards.toModel(zeroTokens: ApiZeroTokens?): UserRewards {
    val legacyRewards = legacyRewards
    val dailyRewards = totalDailyRewards
    val referralRewards = totalReferralFees
    val unClaimedRewards = unclaimedRewards.toBigDecimalOrZero()
    val combinedRewards = legacyRewards.toBigDecimalOrZero()
        .plus(dailyRewards.toBigDecimalOrZero())
        .plus(referralRewards.toBigDecimalOrZero())

    return UserRewards(
        zero = combinedRewards.toString(),
        zeroPreviousDay = zeroPreviousDay,
        unclaimedRewards = unclaimedRewards,
        decimals = decimals,
        price = zeroTokens?.price ?: 0.0,
        reference = zeroTokens?.reference ?: "",
        hasUnclaimedRewards = unClaimedRewards.isPositive()
    )
}

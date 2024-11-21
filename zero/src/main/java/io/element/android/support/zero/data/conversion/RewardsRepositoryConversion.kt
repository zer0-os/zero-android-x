package io.element.android.support.zero.data.conversion

import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiUserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens

internal fun ApiUserRewards.toModel(zeroTokens: ApiZeroTokens?) =
    UserRewards(
        zero = zero,
        zeroPreviousDay = zeroPreviousDay,
        decimals = decimals,
        price = zeroTokens?.price ?: 0.0,
        reference = zeroTokens?.reference ?: ""
    )

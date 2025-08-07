package io.element.android.libraries.matrix.impl.conversion

import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens

fun UserRewards.map() = ZeroUserRewards(
    zero = this.zero,
    decimals = this.decimals,
    price = this.price,
    unclaimedRewards = this.unclaimedRewards ?: "0",
    hasUnclaimedRewards = this.hasUnclaimedRewards ?: false
)

fun ApiZeroTokens.toModel() = ZeroMeowPrice(price, reference, diff)

package io.element.android.libraries.matrix.impl.conversion

import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.data.model.UserRewards

fun UserRewards.map() = ZeroUserRewards(
    zero = this.zero, decimals = this.decimals, price = this.price
)

package io.element.android.features.zerorewards.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Immutable
data class RewardsModalState(
    val userRewards: ZeroUserRewards = ZeroUserRewards.empty()
)

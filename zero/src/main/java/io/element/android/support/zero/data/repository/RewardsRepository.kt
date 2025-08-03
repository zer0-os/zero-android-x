package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens
import kotlinx.coroutines.flow.StateFlow

interface RewardsRepository {

    val shouldShowNewRewardsIntimation: StateFlow<Boolean>

    val userRewards: StateFlow<UserRewards>

    suspend fun getMyRewards(shouldCheckRewardsIntimation: Boolean = false)

    suspend fun getMeowPrice(): ApiZeroTokens

    suspend fun dismissRewardsIntimation()
}

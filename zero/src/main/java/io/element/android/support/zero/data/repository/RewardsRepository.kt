package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.UserRewards
import kotlinx.coroutines.flow.Flow

interface RewardsRepository {

    val shouldShowNewRewardsIntimation: Flow<Boolean>

    suspend fun getMyRewards(shouldCheckRewardsIntimation: Boolean = false): Flow<UserRewards>
}

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.response.ApiUserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens
import retrofit2.http.GET

interface ZeroRewardService {

    @GET(value = "rewards/mine")
    suspend fun fetchMyRewards(): ApiUserRewards

    @GET(value = "api/tokens/zero")
    suspend fun fetchZeroTokens(): ApiZeroTokens
}

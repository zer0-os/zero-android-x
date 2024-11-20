package io.element.android.support.zero.data.repository

import io.element.android.support.zero.common.extension.channelFlowWithAwait
import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.ApiUserRewards
import io.element.android.support.zero.network.model.response.ApiZeroTokens
import io.element.android.support.zero.network.service.ZeroRewardService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RewardsRepositoryImpl(
    private val preferences: Preferences,
    private val zeroRewardService: ZeroRewardService
) : RewardsRepository {

    private val _shouldShouldNewRewardsIntimation = MutableStateFlow(false)
    override val shouldShowNewRewardsIntimation: Flow<Boolean> =
        _shouldShouldNewRewardsIntimation

    override suspend fun getMyRewards(shouldCheckRewardsIntimation: Boolean) =
        channelFlowWithAwait {
            val existingRewards = preferences.userRewards()
            if (shouldCheckRewardsIntimation) {
                trySend(existingRewards)
            }
            val apiZeroRewards =
                awaitAll(
                    async { zeroRewardService.fetchMyRewards() },
                    async { zeroRewardService.fetchZeroTokens() }
                )
            val userRewards = (apiZeroRewards.first() as? ApiUserRewards)
                ?.toModel(apiZeroRewards[1] as? ApiZeroTokens)

            userRewards?.let {
                val lastZeroCredits = parseZeroCredits(existingRewards)
                val newZeroCredits = parseZeroCredits(it)
                val earnedCredits = newZeroCredits.minus(lastZeroCredits)
                it.earnedRewards = earnedCredits

                preferences.saveUserRewards(it)
                if (shouldCheckRewardsIntimation) {
                    _shouldShouldNewRewardsIntimation.emit(earnedCredits > 0)
                }
                trySend(it)
            }
        }

    private fun parseZeroCredits(userRewards: UserRewards): Double {
        return parseCredits(userRewards.zero, userRewards.decimals)
    }

    private fun parseCredits(credits: String, decimals: Int): Double {
        return try {
            val delimiter = credits.length - decimals
            credits.substring(0, delimiter).toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
}

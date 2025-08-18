package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.reward.ApiUserRewards
import io.element.android.support.zero.network.model.response.reward.ApiZeroTokens
import io.element.android.support.zero.network.service.ZeroRewardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class RewardsRepositoryImpl(
    private val preferences: Preferences,
    private val zeroRewardService: ZeroRewardService
) : RewardsRepository {

    private val _shouldShouldNewRewardsIntimation = MutableStateFlow(false)
    override val shouldShowNewRewardsIntimation: StateFlow<Boolean> =
        _shouldShouldNewRewardsIntimation

    private val _userRewards = MutableStateFlow(preferences.userRewards())
    override val userRewards: StateFlow<UserRewards> = _userRewards

    override suspend fun getMyRewards(shouldCheckRewardsIntimation: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching {
                val existingRewards = preferences.userRewards()
                val apiZeroRewards =
                    awaitAll(
                        async { zeroRewardService.fetchMyRewards() },
                        async { getMeowPrice() }
                    )
                val userRewards = (apiZeroRewards.first() as? ApiUserRewards)
                    ?.toModel(apiZeroRewards[1] as? ApiZeroTokens)

                userRewards?.let {
                    val lastZeroCredits = parseZeroCredits(existingRewards)
                    val newZeroCredits = parseZeroCredits(it)
                    val earnedCredits = newZeroCredits.minus(lastZeroCredits)
                    it.earnedRewards = earnedCredits

                    preferences.saveUserRewards(it)
                    _userRewards.emit(it)
                    if (shouldCheckRewardsIntimation) {
                        _shouldShouldNewRewardsIntimation.emit(earnedCredits > 0)
                    }
                }
            }
        }
    }

    override suspend fun getMeowPrice(): ApiZeroTokens {
        return zeroRewardService.fetchZeroTokens()
    }

    override suspend fun dismissRewardsIntimation() {
        _shouldShouldNewRewardsIntimation.emit(false)
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

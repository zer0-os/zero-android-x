package io.element.android.support.zero.common.state

import io.element.android.support.zero.common.util.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object StateBus {

	private val userStateFlow = MutableStateFlow(UserState.UNIDENTIFIED)
	val userStateObservable: Flow<UserState> = userStateFlow
	val userState: UserState
		get() = userStateFlow.value

	fun onUserStateChanged(state: UserState) {
		userStateFlow.value = state
	}

    private val claimRewardsStateFlow = MutableStateFlow(false)
    val claimRewardsStateObservable: Flow<Boolean> = claimRewardsStateFlow

    fun onClaimUserRewards() {
        claimRewardsStateFlow.value = true
    }

    fun onRewardsClaimed() {
        claimRewardsStateFlow.value = false
    }
}

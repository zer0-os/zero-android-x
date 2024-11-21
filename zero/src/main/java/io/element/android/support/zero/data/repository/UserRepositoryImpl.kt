package io.element.android.support.zero.data.repository

import io.element.android.support.zero.common.extension.channelFlowWithAwait
import io.element.android.support.zero.network.model.request.EditUserProfileRequest
import io.element.android.support.zero.network.model.request.UsersFilter
import io.element.android.support.zero.network.service.ZeroUserService
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val zeroUserService: ZeroUserService
) : UserRepository {
    override suspend fun getUsers(filterName: String?): Flow<List<String>> = channelFlowWithAwait {
        if (filterName.isNullOrEmpty()) {
            trySend(emptyList())
            return@channelFlowWithAwait
        }

        // Just relying on network result in order to tackle FTS with ranking
        val filter = UsersFilter.newNameFilter(filterName).toString()
        zeroUserService
            .getUsers(filter)
            ?.takeIf { it.isNotEmpty() }
            ?.let { zeroUsers ->
                trySend(zeroUsers.mapNotNull { it.matrixId })
            }
            ?: trySend(emptyList())
    }

    override suspend fun updateUserProfile(userName: String?, avatarUrl: String?, profileZId: String?) {
        zeroUserService.updateProfile(
            EditUserProfileRequest.newRequest(
                firstName = userName,
                image = avatarUrl,
                profileZid = profileZId
            )
        )
    }
}

package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import io.element.android.support.zero.common.extension.channelFlowWithAwait
import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.network.model.request.EditUserProfileRequest
import io.element.android.support.zero.network.model.request.MatrixUsersFilter
import io.element.android.support.zero.network.model.request.UsersFilter
import io.element.android.support.zero.network.service.ZeroMatrixUserService
import io.element.android.support.zero.network.service.ZeroUserService
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val zeroUserService: ZeroUserService,
    private val zeroMatrixUserService: ZeroMatrixUserService,
) : UserRepository {

    override suspend fun getCurrentUser(): Flow<ZeroUser> = channelFlowWithAwait {
        val user = zeroUserService.getCurrentUser()
        trySend(user.toModel())
    }

    override suspend fun getUsers(filterName: String?): Flow<List<ZeroUser>> = channelFlowWithAwait {
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
                trySend(zeroUsers.map { it.toModel() })
            }
            ?: trySend(emptyList())
    }

    override suspend fun getUser(userId: String): Flow<ZeroUser> =
        channelFlowWithAwait {
            runCatching {
                val apiUser = zeroMatrixUserService.getMatrixUsers(
                    MatrixUsersFilter.newFilter(listOf(userId))
                ).firstOrNull()
                apiUser?.let {
                    trySend(it.toModel())
                }
            }
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

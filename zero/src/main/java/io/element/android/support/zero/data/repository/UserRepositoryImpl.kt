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
import timber.log.Timber

class UserRepositoryImpl(
    private val zeroUserService: ZeroUserService,
    private val zeroMatrixUserService: ZeroMatrixUserService,
) : UserRepository {

    override suspend fun getCurrentUser(): Flow<ZeroUser> = channelFlowWithAwait {
        runSafeCall {
            val user = zeroUserService.getCurrentUser()
            trySend(user.toModel())
        }
    }

    override suspend fun getUsers(filterName: String?): Flow<List<ZeroUser>> = channelFlowWithAwait {
        if (filterName.isNullOrEmpty()) {
            trySend(emptyList())
            return@channelFlowWithAwait
        }

        runSafeCall {
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
    }

    override suspend fun getUser(userId: String): Flow<ZeroUser?> =
        channelFlowWithAwait {
            runSafeCall {
                val apiUser = zeroMatrixUserService.getMatrixUsers(
                    MatrixUsersFilter.newFilter(listOf(userId))
                ).firstOrNull()
                trySend(apiUser?.toModel())
            }
        }

    override suspend fun getUsers(userIds: List<String>): List<ZeroUser> {
        val result = runCatching {
            val apiUsers = zeroMatrixUserService.getMatrixUsers(
                MatrixUsersFilter.newFilter(userIds)
            )
            apiUsers.map { it.toModel() }
        }
        return result.getOrDefault(emptyList())
    }

    override suspend fun updateUserProfile(userName: String?, avatarUrl: String?, profileZId: String?) {
        runSafeCall {
            zeroUserService.updateProfile(
                EditUserProfileRequest.newRequest(
                    firstName = userName,
                    image = avatarUrl,
                    profileZid = profileZId
                )
            )
        }
    }

    private suspend fun <T> runSafeCall(run: suspend () -> T) =
        try {
            run()
        } catch (e: Throwable) {
            Timber.e(e)
        }
}

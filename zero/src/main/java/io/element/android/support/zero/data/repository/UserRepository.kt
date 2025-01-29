package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): Flow<ZeroUser>

    suspend fun getUsers(filterName: String? = null): Flow<List<ZeroUser>>

    suspend fun getUser(userId: String): Flow<ZeroUser?>

    suspend fun getUsers(userIds: List<String>): List<ZeroUser>

    suspend fun updateUserProfile(
        userName: String? = null,
        avatarUrl: String? = null,
        profileZId: String? = null
    )
}

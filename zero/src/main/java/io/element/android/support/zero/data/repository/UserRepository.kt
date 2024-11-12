package io.element.android.support.zero.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUsers(filterName: String? = null): Flow<List<String>>

    suspend fun updateUserProfile(
        userName: String? = null,
        avatarUrl: String? = null,
        profileZId: String? = null
    )
}

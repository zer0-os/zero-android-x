package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.AuthSSOToken
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<AuthSSOToken>

    suspend fun saveMatrixLoginInfo(token: String, userId: String)

    suspend fun createAndAuthorise(
        email: String,
        password: String,
        inviteSlug: String
    ): Boolean

    suspend fun completeProfile(
        inviteCode: String,
        name: String,
        profileImage: File? = null
    ): Flow<AuthSSOToken>

    suspend fun logout()
}

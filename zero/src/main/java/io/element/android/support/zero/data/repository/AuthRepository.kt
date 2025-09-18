package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
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
    ): Flow<AuthSSOToken>

    suspend fun completeSignUp(
        inviteCode: String,
        displayName: String,
        avatarUrl: String?
    ): Flow<ZeroUser>

    suspend fun linkZeroUser(fromCreateAccountFlow: Boolean, matrixUserId: String)

    suspend fun loginWithWallet(walletToken: String): Flow<AuthSSOToken>

    suspend fun loginWithOAuth(oAuthToken: String): Flow<AuthSSOToken>

    suspend fun signUpWithWallet(walletToken: String, inviteSlug: String): Flow<AuthSSOToken>

    suspend fun logout()

    suspend fun resetPasswordRequest(email: String): Flow<Unit>
}

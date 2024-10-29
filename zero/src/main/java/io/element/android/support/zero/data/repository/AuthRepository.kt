package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.AuthSSOToken
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<AuthSSOToken>

    suspend fun logout()
}

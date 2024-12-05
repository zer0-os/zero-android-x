package io.element.android.support.zero.data.repository

import io.element.android.support.zero.common.extension.channelFlowWithAwait
import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.model.AuthSSOToken
import io.element.android.support.zero.network.model.request.AuthoriseUserRequest
import io.element.android.support.zero.network.model.request.CreateAndAuthoriseUserRequest
import io.element.android.support.zero.network.model.response.ZeroAuthCredentials
import io.element.android.support.zero.network.service.ZeroAuthService
import kotlinx.coroutines.flow.Flow
import java.io.File

class AuthRepositoryImpl(
    private val preferences: Preferences,
    private val zeroAuthService: ZeroAuthService,
    private val dataCleaner: DataCleaner,
) : AuthRepository {
    override suspend fun login(email: String, password: String) = channelFlowWithAwait {
        runSafeCall {
            val payload = AuthoriseUserRequest.newRequest(email, password)
            val zeroAuthRequest = zeroAuthService.authorise(payload)
            val ssoToken = proceedLoginFlow(zeroAuthRequest)
            trySend(ssoToken)
        }
    }

    private suspend fun proceedLoginFlow(authCredentials: ZeroAuthCredentials): AuthSSOToken {
        preferences.setZeroToken(authCredentials.accessToken)
        val ssoRequest = zeroAuthService.getSSOToken()
        return ssoRequest.toModel()
    }

    override suspend fun saveMatrixLoginInfo(token: String, userId: String) {
        preferences.setMatrixToken(token)
        preferences.setUserId(userId)
    }

    override suspend fun createAndAuthorise(email: String, password: String, inviteSlug: String): Boolean {
        return runSafeCall {
            val nonce = zeroAuthService.authenticateNonce()
            val payload = CreateAndAuthoriseUserRequest.newRequest(email, password, inviteSlug)
            val credentials =
                zeroAuthService
                    .createAndAuthorise(nonceToken = nonce.nonceHeader, payload = payload)
            preferences.setZeroToken(credentials.accessToken)
            return@runSafeCall credentials.accessToken.isNotBlank()
        }
    }

    override suspend fun completeProfile(inviteCode: String, name: String, profileImage: File?): Flow<AuthSSOToken> =
        channelFlowWithAwait {
            trySend(AuthSSOToken(""))
        }

    override suspend fun logout() {
        dataCleaner.clean()
    }

    private suspend fun <T> runSafeCall(run: suspend () -> T) =
        try {
            run()
        } catch (e: Throwable) {
            throw e
        }
}

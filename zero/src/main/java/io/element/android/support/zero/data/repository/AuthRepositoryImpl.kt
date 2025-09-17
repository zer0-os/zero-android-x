package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import io.element.android.support.zero.common.extension.channelFlowWithAwait
import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.delegate.DataCleaner
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.data.model.AuthSSOToken
import io.element.android.support.zero.network.model.request.AuthoriseUserRequest
import io.element.android.support.zero.network.model.request.CreateAndAuthoriseUserRequest
import io.element.android.support.zero.network.model.request.FinaliseCreateAccountRequest
import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import io.element.android.support.zero.network.model.response.auth.ZeroAuthCredentials
import io.element.android.support.zero.network.service.ZeroAuthService
import io.element.android.support.zero.network.service.ZeroUserService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class AuthRepositoryImpl(
    private val preferences: Preferences,
    private val zeroAuthService: ZeroAuthService,
    private val zeroUserService: ZeroUserService,
    private val dataCleaner: DataCleaner,
) : AuthRepository {
    override suspend fun login(email: String, password: String) = channelFlowWithAwait {
        val payload = AuthoriseUserRequest.newRequest(email, password)
        val zeroAuthRequest = zeroAuthService.authorise(payload)
        val ssoToken = proceedLoginFlow(zeroAuthRequest)
        trySend(ssoToken)
    }

    override suspend fun saveMatrixLoginInfo(token: String, userId: String) {
        preferences.setMatrixToken(token)
        preferences.setUserId(userId)
    }

    override suspend fun createAndAuthorise(email: String, password: String, inviteSlug: String): Flow<AuthSSOToken> =
        channelFlowWithAwait {
            val nonce = zeroAuthService.authenticateNonce()
            val payload = CreateAndAuthoriseUserRequest.newRequest(email, password, inviteSlug)
            val credentials =
                zeroAuthService
                    .createAndAuthorise(nonceToken = nonce.nonceHeader, payload = payload)
            val ssoToken = proceedLoginFlow(credentials)
            trySend(ssoToken)
        }

    private suspend fun proceedLoginFlow(authCredentials: ZeroAuthCredentials): AuthSSOToken {
        preferences.setZeroToken(authCredentials.accessToken)
        val ssoRequest = zeroAuthService.getSSOToken()
        return ssoRequest.toModel()
    }

    override suspend fun completeSignUp(
        inviteCode: String, displayName: String, avatarUrl: String?
    ): Flow<ZeroUser> = channelFlowWithAwait {
        val userId = preferences.userId().cleanedZeroId()
        val payload = FinaliseCreateAccountRequest(
            inviteCode = inviteCode,
            name = displayName,
            profileImage = avatarUrl,
            userId = userId
        )
        val inviter = zeroAuthService.finaliseSignUp(payload)
        trySend(inviter.inviter.toModel())
    }

    override suspend fun linkZeroUser(fromCreateAccountFlow: Boolean, matrixUserId: String) {
        runSafeCall {
            val currentUser = zeroUserService.getCurrentUser()
            if (fromCreateAccountFlow || currentUser.matrixId.isNullOrBlank()) {
                val payload = LinkZeroUserRequest.newRequest(matrixUserId = matrixUserId)
                zeroAuthService.linkZeroUser(payload)
            }
        }
    }

    override suspend fun loginWithWallet(walletToken: String) = channelFlowWithAwait {
        val credentials = zeroAuthService.nonceOrAuthorise(walletToken)
        val ssoToken = proceedLoginFlow(credentials)
        trySend(ssoToken)
    }

    override suspend fun loginWithOAuth(oAuthToken: String) = channelFlowWithAwait {
        val credentials = zeroAuthService.establishZeroOAuthSession(oAuthToken)
        val ssoToken = proceedLoginFlow(credentials)
        trySend(ssoToken)
    }

    override suspend fun signUpWithWallet(walletToken: String, inviteSlug: String) =
        channelFlowWithAwait {
            val payload = CreateAndAuthoriseUserRequest(inviteSlug = inviteSlug)
            val credentials =
                zeroAuthService
                    .createAndAuthorise(nonceToken = walletToken, payload = payload)
            val ssoToken = proceedLoginFlow(credentials)
            trySend(ssoToken)
        }

    override suspend fun logout() {
        dataCleaner.clean()
    }

    private suspend fun <T> runSafeCall(run: suspend () -> T) =
        try {
            run()
        } catch (e: Throwable) {
            Timber.e(e)
        }
}

private fun String.cleanedZeroId() =
    substringAfter("@").substringBefore(":")

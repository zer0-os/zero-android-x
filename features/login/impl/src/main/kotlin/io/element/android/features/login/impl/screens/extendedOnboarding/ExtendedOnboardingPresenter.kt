/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class ExtendedOnboardingPresenter(
    @Assisted private val params: ExtendedOnboardingNode.Inputs,
    private val authenticationService: MatrixAuthenticationService,
) : Presenter<ExtendedOnboardingState> {

    @AssistedFactory
    interface Factory {
        fun create(
            params: ExtendedOnboardingNode.Inputs,
        ): ExtendedOnboardingPresenter
    }

    @Composable
    override fun present(): ExtendedOnboardingState {
        val localCoroutineScope = rememberCoroutineScope()
        val actionState: MutableState<AsyncAction<Unit>> = remember {
            mutableStateOf(AsyncAction.Uninitialized)
        }

        val forgotPasswordEmail = remember { mutableStateOf("") }

        fun handleEvents(event: ExtendedOnboardingEvents) {
            when (event) {
                is ExtendedOnboardingEvents.SetForgotPasswordEmail -> {
                    forgotPasswordEmail.value = event.email
                }
                is ExtendedOnboardingEvents.SubmitSSO ->
                    localCoroutineScope.submitSSO(params.userEmail, event.otp, actionState)
                ExtendedOnboardingEvents.Submit ->
                    localCoroutineScope.submitForgotPassword(forgotPasswordEmail.value, actionState)
                ExtendedOnboardingEvents.ResendOTP ->
                    localCoroutineScope.resentOtp(params.userEmail, actionState)
                ExtendedOnboardingEvents.ClearError ->
                    actionState.value = AsyncAction.Uninitialized
            }
        }

        return ExtendedOnboardingState(
            forgotPasswordEmail = forgotPasswordEmail.value,
            actionState = actionState.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.submitForgotPassword(
        email: String,
        actionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        actionState.value = AsyncAction.Loading
        authenticationService.requestResetPassword(email)
            .onSuccess {
                actionState.value = AsyncAction.Success(Unit)
            }
            .onFailure { failure ->
                actionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.submitSSO(
        email: String,
        code: String,
        actionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        actionState.value = AsyncAction.Loading
        authenticationService.verifyOtp(email, code)
            .onSuccess {
                actionState.value = AsyncAction.Uninitialized
            }
            .onFailure { failure ->
                actionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.resentOtp(
        email: String,
        actionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        actionState.value = AsyncAction.Loading
        authenticationService.requestOtp(email)
            .onSuccess {
                actionState.value = AsyncAction.Uninitialized
            }
            .onFailure { failure ->
                actionState.value = AsyncAction.Failure(failure)
            }
    }
}

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
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class ExtendedOnboardingPresenter(
    private val authenticationService: MatrixAuthenticationService,
): Presenter<ExtendedOnboardingState> {

    @Composable
    override fun present(): ExtendedOnboardingState {
        val localCoroutineScope = rememberCoroutineScope()
        val forgotPasswordAction: MutableState<AsyncAction<Unit>> = remember {
            mutableStateOf(AsyncAction.Uninitialized)
        }

        val forgotPasswordEmail = remember { mutableStateOf("") }

        fun handleEvents(event: ExtendedOnboardingEvents) {
            when (event) {
                is ExtendedOnboardingEvents.SetForgotPasswordEmail -> {
                    forgotPasswordEmail.value = event.email
                }
                ExtendedOnboardingEvents.Submit ->
                    localCoroutineScope.submitForgotPassword(forgotPasswordEmail.value, forgotPasswordAction)
                ExtendedOnboardingEvents.ClearError ->
                    forgotPasswordAction.value = AsyncAction.Uninitialized
            }
        }

        return ExtendedOnboardingState(
            forgotPasswordEmail = forgotPasswordEmail.value,
            forgotPasswordAction = forgotPasswordAction.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.submitForgotPassword(
        email: String,
        forgotPasswordAction: MutableState<AsyncAction<Unit>>
    ) = launch {
        forgotPasswordAction.value = AsyncAction.Loading
        authenticationService.requestResetPassword(email)
            .onSuccess {
                forgotPasswordAction.value = AsyncAction.Success(Unit)
            }
            .onFailure { failure ->
                forgotPasswordAction.value = AsyncAction.Failure(failure)
            }
    }
}

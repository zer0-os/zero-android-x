/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.verifyzeroinvite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.login.impl.login.InvalidZeroInviteCode
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class VerifyZeroInvitePresenter(
    private val authenticationService: MatrixAuthenticationService,
) : Presenter<VerifyZeroInviteState> {

    @Composable
    override fun present(): VerifyZeroInviteState {
        val localCoroutineScope = rememberCoroutineScope()
        val actionState: MutableState<AsyncAction<Unit>> = remember {
            mutableStateOf(AsyncAction.Uninitialized)
        }

        val inviteCode = remember { mutableStateOf("") }

        fun handleEvents(event: VerifyZeroInviteEvents) {
            when (event) {
                is VerifyZeroInviteEvents.OnInviteEdit -> inviteCode.value = event.invite
                VerifyZeroInviteEvents.Submit ->
                    localCoroutineScope.verifyInvite(inviteCode.value, actionState)
                VerifyZeroInviteEvents.ClearError ->
                    actionState.value = AsyncAction.Uninitialized
            }
        }

        return VerifyZeroInviteState(
            inviteCode = inviteCode.value,
            actionState = actionState.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.verifyInvite(
        inviteCode: String,
        actionState: MutableState<AsyncAction<Unit>>,
    ) = launch {
        actionState.value = AsyncAction.Loading
        authenticationService.validateInviteCode(inviteCode)
            .onSuccess { isCodeValid ->
                if (isCodeValid) {
                    actionState.value = AsyncAction.Success(Unit)
                } else {
                    actionState.value = AsyncAction.Failure(InvalidZeroInviteCode())
                }
            }
            .onFailure { failure ->
                actionState.value = AsyncAction.Failure(failure)
            }
    }
}

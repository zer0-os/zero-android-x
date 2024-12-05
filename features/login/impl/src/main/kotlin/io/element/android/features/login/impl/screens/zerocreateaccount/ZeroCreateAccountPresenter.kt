/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.features.login.impl.DefaultLoginUserStory
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ZeroCreateAccountPresenter @AssistedInject constructor(
    @Assisted private val params: Params,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val authenticationService: MatrixAuthenticationService,
    private val defaultLoginUserStory: DefaultLoginUserStory,
) : Presenter<ZeroCreateAccountState> {
    data class Params(
        val inviteCode: String
    )

    @AssistedFactory
    interface Factory {
        fun create(params: Params): ZeroCreateAccountPresenter
    }

    @Composable
    override fun present(): ZeroCreateAccountState {
        val localCoroutineScope = rememberCoroutineScope()
        val createAccountAction: MutableState<AsyncData<SessionId>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        val formState = rememberSaveable {
            mutableStateOf(ZeroCreateAccountFormState.Default)
        }
        val accountProvider by accountProviderDataSource.flow().collectAsState()

        fun handleEvents(event: ZeroCreateAccountEvents) {
            when (event) {
                is ZeroCreateAccountEvents.SetEmail -> updateFormState(formState) {
                    copy(email = event.email)
                }
                is ZeroCreateAccountEvents.SetPassword -> updateFormState(formState) {
                    copy(password = event.password)
                }
                is ZeroCreateAccountEvents.SetConfirmPassword -> updateFormState(formState) {
                    copy(confirmPassword = event.confirmPassword)
                }
                ZeroCreateAccountEvents.Submit -> {
                    localCoroutineScope.submit(formState.value, accountProvider.url, createAccountAction)
                }
                ZeroCreateAccountEvents.ClearError -> createAccountAction.value = AsyncData.Uninitialized
            }
        }

        return ZeroCreateAccountState(
            inviteCode = params.inviteCode,
            formState = formState.value,
            createAccountAction = createAccountAction.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.submit(
        formState: ZeroCreateAccountFormState,
        homeserverUrl: String,
        createAccountActionState: MutableState<AsyncData<SessionId>>
    ) = launch {
        createAccountActionState.value = AsyncData.Loading()
        authenticationService.setHomeserver(homeserverUrl)
            .onSuccess {
                authenticationService.createZeroAccountAndAuthorise(
                    email = formState.email,
                    password = formState.password,
                    inviteCode = params.inviteCode
                )
                    .onSuccess { sessionId ->
                        defaultLoginUserStory.setLoginFlowIsDone(true)
                        createAccountActionState.value = AsyncData.Success(sessionId)
                    }
                    .onFailure { failure ->
                        createAccountActionState.value = AsyncData.Failure(failure)
                    }
            }
            .onFailure { failure ->
                createAccountActionState.value = AsyncData.Failure(failure)
            }
    }

    private fun updateFormState(
        formState: MutableState<ZeroCreateAccountFormState>,
        updateLambda: ZeroCreateAccountFormState.() -> ZeroCreateAccountFormState
    ) {
        formState.value = updateLambda(formState.value)
    }
}

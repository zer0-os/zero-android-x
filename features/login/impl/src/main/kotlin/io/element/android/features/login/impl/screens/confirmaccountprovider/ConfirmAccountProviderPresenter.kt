/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.login.LoginHelper
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.support.zero.common.extension.withScope
import kotlinx.coroutines.Dispatchers

class ConfirmAccountProviderPresenter @AssistedInject constructor(
    @Assisted private val params: Params,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val loginHelper: LoginHelper,
) : Presenter<ConfirmAccountProviderState> {
    data class Params(
        val isAccountCreation: Boolean,
    )

    @AssistedFactory
    interface Factory {
        fun create(params: Params): ConfirmAccountProviderPresenter
    }

    @Composable
    override fun present(): ConfirmAccountProviderState {
        val accountProvider by accountProviderDataSource.flow.collectAsState()
        val localCoroutineScope = rememberCoroutineScope()

        val loginMode by loginHelper.collectLoginMode()

        fun handleEvents(event: ConfirmAccountProviderEvents) {
            when (event) {
                ConfirmAccountProviderEvents.Continue -> {
                    loginHelper.submit(
                        coroutineScope = localCoroutineScope,
                        isAccountCreation = params.isAccountCreation,
                        homeserverUrl = accountProvider.url,
                        loginHint = null,
                    )
                }
                ConfirmAccountProviderEvents.ClearError -> loginHelper.clearError()
                is ConfirmAccountProviderEvents.ValidateInvite -> {
                    validateInviteCode(event.inviteCode, loginFlowAction)
                }
            }
        }

        return ConfirmAccountProviderState(
            accountProvider = accountProvider,
            isAccountCreation = params.isAccountCreation,
            loginMode = loginMode,
            eventSink = ::handleEvents
        )
    }

    private fun validateInviteCode(
        inviteCode: String,
        loginFlowAction: MutableState<AsyncData<LoginFlow>>,
    ) {
        loginFlowAction.value = AsyncData.Loading()
        withScope(Dispatchers.IO) {
            val result = authenticationService.validateInviteCode(inviteCode)
            val isCodeValid = result.getOrNull() ?: false
            if (isCodeValid) {
                loginFlowAction.value = AsyncData.Success(
                    LoginFlow.ZeroCreateAccountFlow(inviteCode)
                )
            } else {
                loginFlowAction.value = AsyncData.Failure(InvalidZeroInviteCode())
            }
        }
    }
}

internal class InvalidZeroInviteCode: Exception()

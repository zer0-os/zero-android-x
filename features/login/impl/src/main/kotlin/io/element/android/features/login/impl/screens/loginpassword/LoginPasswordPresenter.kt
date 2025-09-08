/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.loginpassword

import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.ui.components.button.rememberAppKitState
import dev.zacsweers.metro.Inject
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.walletconnect.WalletConnectDelegate
import io.element.android.features.login.impl.walletconnect.WalletConnectService
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
@Inject
class LoginPasswordPresenter(
    private val authenticationService: MatrixAuthenticationService,
    private val accountProviderDataSource: AccountProviderDataSource,
) : Presenter<LoginPasswordState> {
    @Composable
    override fun present(): LoginPasswordState {
        val localCoroutineScope = rememberCoroutineScope()
        val loginAction: MutableState<AsyncData<SessionId>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        val formState = rememberSaveable {
            mutableStateOf(LoginFormState.Default)
        }
        val accountProvider by accountProviderDataSource.flow.collectAsState()

        LaunchedEffect(Unit) {
            WalletConnectDelegate.wcEventModels.collectLatest { model ->
                when (model) {
                    is Modal.Model.ApprovedSession -> {
                        WalletConnectService.requestPersonalSign()
                    }
                    is Modal.Model.SessionRequestResponse -> {
                        when (model.result) {
                            is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                                val successResult = (model.result as Modal.Model.JsonRpcResponse.JsonRpcResult)
                                onWalletConnectionSuccess(successResult, loginAction)
                            }
                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                val errorResult = (model.result as Modal.Model.JsonRpcResponse.JsonRpcError)
                                onWalletConnectError(loginAction)
                            }
                        }
                    }
                    is Modal.Model.RejectedSession -> onWalletConnectError(loginAction)
                    is Modal.Model.Error -> onWalletConnectError(loginAction)
                    else -> {
                        // do nothing for now
                    }
                }
            }
        }

        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        val bottomSheetNavigator = BottomSheetNavigator(sheetState)
        val navController = rememberNavController(bottomSheetNavigator)
        val web3ModalState = rememberAppKitState(navController = navController)
        val isWeb3Connected: Boolean by web3ModalState.isConnected.collectAsState(initial = false)
        val showWeb3Modal = rememberSaveable { mutableStateOf(false) }

        fun handleEvents(event: LoginPasswordEvents) {
            when (event) {
                is LoginPasswordEvents.SetLogin -> updateFormState(formState) {
                    copy(login = event.login)
                }
                is LoginPasswordEvents.SetPassword -> updateFormState(formState) {
                    copy(password = event.password)
                }
                LoginPasswordEvents.Submit -> {
                    localCoroutineScope.submit(formState.value, loginAction)
                }
                LoginPasswordEvents.ClearError -> loginAction.value = AsyncData.Uninitialized
                is LoginPasswordEvents.ToggleWeb3Modal -> {
                    if (event.show) {
                        if (isWeb3Connected) {
                            AppKit.disconnect(
                                onSuccess = { showWeb3Modal.value = true },
                                onError = { onWalletConnectError(loginAction) }
                            )
                        } else {
                            showWeb3Modal.value = true
                        }
                    } else {
                        showWeb3Modal.value = false
                    }
                }
            }
        }

        return LoginPasswordState(
            accountProvider = accountProvider,
            formState = formState.value,
            loginAction = loginAction.value,
            showWeb3Modal = showWeb3Modal.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.submit(formState: LoginFormState, loggedInState: MutableState<AsyncData<SessionId>>) = launch {
        loggedInState.value = AsyncData.Loading()
        authenticationService.loginWithZero(formState.login.trim(), formState.password)
            .onSuccess { sessionId ->
                loggedInState.value = AsyncData.Success(sessionId)
            }
            .onFailure { failure ->
                loggedInState.value = AsyncData.Failure(failure)
            }
    }

    private fun updateFormState(formState: MutableState<LoginFormState>, updateLambda: LoginFormState.() -> LoginFormState) {
        formState.value = updateLambda(formState.value)
    }

    private fun CoroutineScope.onWalletConnectionSuccess(
        successResult: Modal.Model.JsonRpcResponse.JsonRpcResult,
        loggedInState: MutableState<AsyncData<SessionId>>
    ) = launch {
        loggedInState.value = AsyncData.Loading()
        val web3Token = "Web3 ${successResult.result}"
        authenticationService.loginWithWeb3(web3Token)
            .onSuccess { sessionId ->
                loggedInState.value = AsyncData.Success(sessionId)
            }
            .onFailure { failure ->
                loggedInState.value = AsyncData.Failure(failure)
            }
    }

    private fun onWalletConnectError(loggedInState: MutableState<AsyncData<SessionId>>) {
        loggedInState.value = AsyncData.Failure(Throwable("Wallet connection failed. Please try again."))
    }
}

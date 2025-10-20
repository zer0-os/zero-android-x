/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

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
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.features.login.impl.walletconnect.WalletConnectDelegate
import io.element.android.features.login.impl.walletconnect.WalletConnectService
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.support.zero.common.util.ZeroCreateAccountInviteHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
@AssistedInject
class ZeroCreateAccountPresenter(
    @Assisted private val params: Params,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val authenticationService: MatrixAuthenticationService,
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
        val loginFlowAction: MutableState<AsyncData<LoginMode>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        val formState = rememberSaveable {
            mutableStateOf(ZeroCreateAccountFormState.Default)
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
                                onWalletConnectionSuccess(accountProvider.url, successResult, createAccountAction)
                            }
                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                val errorResult = (model.result as Modal.Model.JsonRpcResponse.JsonRpcError)
                                onWalletConnectError(createAccountAction)
                            }
                        }
                    }
                    is Modal.Model.RejectedSession -> onWalletConnectError(createAccountAction)
                    is Modal.Model.Error -> onWalletConnectError(createAccountAction)
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
                ZeroCreateAccountEvents.OpenLogin -> {
                    localCoroutineScope.proceedToLogin(accountProvider.url, createAccountAction, loginFlowAction)
                }
                is ZeroCreateAccountEvents.ToggleWeb3Modal -> {
                    if (event.show) {
                        if (isWeb3Connected) {
                            AppKit.disconnect(
                                onSuccess = { showWeb3Modal.value = true },
                                onError = { onWalletConnectError(createAccountAction) }
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

        return ZeroCreateAccountState(
            inviteCode = params.inviteCode,
            formState = formState.value,
            createAccountAction = createAccountAction.value,
            loginFlow = loginFlowAction.value,
            showWeb3Modal = showWeb3Modal.value,
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
                        ZeroCreateAccountInviteHolder.inviteCode = params.inviteCode
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

    private fun CoroutineScope.proceedToLogin(
        homeserverUrl: String,
        createAccountAction: MutableState<AsyncData<SessionId>>,
        loginFlowActionState: MutableState<AsyncData<LoginMode>>
    ) = launch {
        createAccountAction.value = AsyncData.Loading()
        authenticationService.setHomeserver(homeserverUrl)
            .onSuccess {
                loginFlowActionState.value = AsyncData.Success(LoginMode.PasswordLogin)
                createAccountAction.value = AsyncData.Uninitialized
            }
            .onFailure { failure ->
                loginFlowActionState.value = AsyncData.Failure(failure)
                createAccountAction.value = AsyncData.Uninitialized
            }
    }

    private fun updateFormState(
        formState: MutableState<ZeroCreateAccountFormState>,
        updateLambda: ZeroCreateAccountFormState.() -> ZeroCreateAccountFormState
    ) {
        formState.value = updateLambda(formState.value)
    }

    private fun CoroutineScope.onWalletConnectionSuccess(
        homeserverUrl: String,
        successResult: Modal.Model.JsonRpcResponse.JsonRpcResult,
        createAccountActionState: MutableState<AsyncData<SessionId>>
    ) = launch {
        createAccountActionState.value = AsyncData.Loading()
        authenticationService.setHomeserver(homeserverUrl)
            .onSuccess {
                val web3Token = "Web3 ${successResult.result}"
                authenticationService.createZeroAccountWithWeb3(web3Token, params.inviteCode)
                    .onSuccess { sessionId ->
                        ZeroCreateAccountInviteHolder.inviteCode = params.inviteCode
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

    private fun onWalletConnectError(createAccountActionState: MutableState<AsyncData<SessionId>>) {
        createAccountActionState.value = AsyncData.Failure(Throwable("Wallet connection failed. Please try again."))
    }
}

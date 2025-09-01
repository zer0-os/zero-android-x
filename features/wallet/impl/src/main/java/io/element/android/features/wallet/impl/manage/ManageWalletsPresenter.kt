/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.ui.components.button.rememberAppKitState
import io.element.android.features.login.impl.walletconnect.WalletConnectDelegate
import io.element.android.features.login.impl.walletconnect.WalletConnectService
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet
import io.element.android.support.zero.common.extension.openExternalUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialNavigationApi::class)
class ManageWalletsPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<ManageWalletsState> {
    @Composable
    override fun present(): ManageWalletsState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val userActionState: MutableState<ManageWalletUserAction> = remember { mutableStateOf(ManageWalletUserAction.None) }

        val currentUser = client.userProfile.collectAsState()
        val userWalletsState: MutableState<List<ZeroWallet>> = remember { mutableStateOf(emptyList()) }

        //start wallet connect
        val web3Token = remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        val bottomSheetNavigator = BottomSheetNavigator(sheetState)
        val navController = rememberNavController(bottomSheetNavigator)
        val web3ModalState = rememberAppKitState(navController = navController)
        val isWeb3Connected: Boolean by web3ModalState.isConnected.collectAsState(initial = false)

        val checkAndConnectWallet: () -> Unit = {
            val connectedAddress = AppKit.getAccount()?.address
            if (isWeb3Connected && !connectedAddress.isNullOrBlank()) {
                userActionState.value = ManageWalletUserAction.LinkWallet(connectedAddress)
            } else {
                AppKit.disconnect(
                    onSuccess = { userActionState.value = ManageWalletUserAction.SelectWallet },
                    onError = { userActionState.value = ManageWalletUserAction.SelectWallet }
                )
            }
        }

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
                                web3Token.value = successResult.result
                                AppKit.getAccount()?.address?.let { walletAddress ->
                                    userActionState.value = ManageWalletUserAction.LinkWallet(walletAddress)
                                }
                            }
                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                val errorResult = (model.result as Modal.Model.JsonRpcResponse.JsonRpcError)
                                onWalletConnectError(genericActionState)
                            }
                        }
                    }
                    is Modal.Model.RejectedSession -> onWalletConnectError(genericActionState)
                    is Modal.Model.Error -> onWalletConnectError(genericActionState)
                    else -> {
                        // do nothing for now
                    }
                }
            }
        }
        //end wallet connect

        LaunchedEffect(Unit) {
            fetchUserWallets(userWalletsState, genericActionState)
        }

        fun handleEvents(event: ManageWalletsEvents) {
            when (event) {
                is ManageWalletsEvents.ShowWallet -> context.openExternalUri(event.wallet.zScanUrl)
                is ManageWalletsEvents.RemoveWallet -> {
                    userActionState.value = ManageWalletUserAction.RemoveWallet(event.wallet.id)
                }
                is ManageWalletsEvents.ConfirmLinkWallet -> {
                    userActionState.value = ManageWalletUserAction.None
                    web3Token.value.takeIf { it.isNotBlank() }?.let { token ->
                        coroutineScope.addWallet(
                            canAuthenticate = event.enableLoggingIn,
                            web3Token = token,
                            genericActionState = genericActionState,
                            onCompletion = {
                                coroutineScope.fetchUserWallets(userWalletsState, genericActionState)
                            }
                        )
                    }
                }
                is ManageWalletsEvents.ConfirmDeleteWallet -> {
                    userActionState.value = ManageWalletUserAction.None
                    coroutineScope.deleteWallet(
                        walletId = event.walletId,
                        genericActionState = genericActionState,
                        onCompletion = {
                            coroutineScope.fetchUserWallets(userWalletsState, genericActionState)
                        }
                    )
                }
                ManageWalletsEvents.CheckAndLinkWallet -> checkAndConnectWallet()
                ManageWalletsEvents.WalletLinkingCancelled,
                ManageWalletsEvents.WalletRemovingCancelled -> {
                    userActionState.value = ManageWalletUserAction.None
                }
                ManageWalletsEvents.HideError -> genericActionState.value = AsyncAction.Success(Unit)
            }
        }

        return ManageWalletsState(
            userId = currentUser.value.userId,
            wallets = userWalletsState.value,
            userActionState = userActionState.value,
            actionState = genericActionState.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.fetchUserWallets(
        userWalletsState: MutableState<List<ZeroWallet>>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.fetchUserWallets()
            .onSuccess {
                userWalletsState.value = it
                genericActionState.value = AsyncAction.Success(Unit)
            }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun CoroutineScope.addWallet(
        canAuthenticate: Boolean,
        web3Token: String,
        genericActionState: MutableState<AsyncAction<Unit>>,
        onCompletion: () -> Unit = {}
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.addWallet(canAuthenticate, web3Token)
            .onSuccess { onCompletion() }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun CoroutineScope.deleteWallet(
        walletId: String,
        genericActionState: MutableState<AsyncAction<Unit>>,
        onCompletion: () -> Unit = {}
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.deleteWallet(walletId)
            .onSuccess { onCompletion() }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun onWalletConnectError(genericActionState: MutableState<AsyncAction<Unit>>) {
        genericActionState.value = AsyncAction.Failure(Throwable("Wallet connection failed. Please try again."))
    }
}

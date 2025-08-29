/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet
import io.element.android.support.zero.common.extension.openExternalUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ManageWalletsPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<ManageWalletsState> {
    @Composable
    override fun present(): ManageWalletsState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val currentUser = client.userProfile.collectAsState()
        val userWalletsState: MutableState<List<ZeroWallet>> = remember { mutableStateOf(emptyList()) }

        LaunchedEffect(Unit) {
            fetchUserWallets(userWalletsState, genericActionState)
        }

        fun handleEvents(event: ManageWalletsEvents) {
            when (event) {
                is ManageWalletsEvents.ShowWallet -> context.openExternalUri(event.wallet.zScanUrl)
                ManageWalletsEvents.HideError -> genericActionState.value = AsyncAction.Success(Unit)
            }
        }

        return ManageWalletsState(
            userId = currentUser.value.userId,
            wallets = userWalletsState.value,
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
}

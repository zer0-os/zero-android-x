/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import javax.inject.Inject

class ManageWalletsPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<ManageWalletsState> {
    @Composable
    override fun present(): ManageWalletsState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val currentUser = client.userProfile.collectAsState()

        LaunchedEffect(Unit) {

        }

        fun handleEvents(event: ManageWalletsEvents) {
//            when (event) {
//
//            }
        }

        return ManageWalletsState(
            userId = currentUser.value.userId,
            eventSink = ::handleEvents
        )
    }
}

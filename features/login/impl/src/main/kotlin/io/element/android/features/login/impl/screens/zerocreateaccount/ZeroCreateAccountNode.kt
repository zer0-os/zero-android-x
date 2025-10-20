/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import com.reown.appkit.client.AppKit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs

@ContributesNode(AppScope::class)
@AssistedInject
class ZeroCreateAccountNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ZeroCreateAccountPresenter.Factory,
) : Node(buildContext, plugins = plugins) {

    data class Inputs(
        val inviteCode: String
    ) : NodeInputs

    private val inputs: Inputs = inputs()
    private val presenter = presenterFactory.create(
        ZeroCreateAccountPresenter.Params(
            inviteCode = inputs.inviteCode
        )
    )

    interface Callback : Plugin {
        fun onLoginPasswordNeeded()
    }

    private fun onProceedToLogin() {
        plugins<Callback>().forEach { it.onLoginPasswordNeeded() }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        return ZeroCreateAccountView(
            modifier = modifier,
            state = state,
            onProceedToLoginScreen = ::onProceedToLogin,
            onBackClick = {
                disconnectWallet()
                navigateUp()
            }
        )
    }

    private fun disconnectWallet() {
        AppKit.disconnect(
            onSuccess = {},
            onError = {}
        )
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.verifyzeroinvite

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import io.element.android.annotations.ContributesNode

@ContributesNode(AppScope::class)
@AssistedInject
class VerifyZeroInviteNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter : VerifyZeroInvitePresenter
) : Node(buildContext, plugins = plugins) {

    interface Callback : Plugin {
        fun onCreateZeroAccount(inviteCode: String)
    }

    private fun onCreateZeroAccount(inviteCode: String) {
        plugins<Callback>().forEach { it.onCreateZeroAccount(inviteCode) }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        VerifyZeroInviteView(
            state = state,
            onBackClick = ::navigateUp,
            onCreateZeroAccount = ::onCreateZeroAccount
        )
    }
}

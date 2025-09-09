/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.Inject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@Inject
class CompleteProfileNode constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: CompleteProfilePresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    interface Callback : NodeInputs {
        fun onProfileUpdated()
    }

    private val callback = inputs<Callback>()

    private val presenter: CompleteProfilePresenter = presenterFactory.create(callback)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        CompleteProfileView(
            state = state,
            modifier = modifier
        )
    }
}

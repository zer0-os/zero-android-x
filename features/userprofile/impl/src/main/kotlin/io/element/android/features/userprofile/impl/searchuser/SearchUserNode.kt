/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.searchuser

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.anvilannotations.ContributesNode
import io.element.android.features.userprofile.api.SearchUserEntryPoint
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.UserId

@ContributesNode(SessionScope::class)
class SearchUserNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: SearchUserPresenter,
) : Node(buildContext, plugins = plugins) {

    private fun onUserSelected(userId: UserId) {
        plugins<SearchUserEntryPoint.Callback>().forEach { it.onUserSelected(userId) }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()

        SearchUserView(
            modifier = modifier,
            state = state,
            onUserSelected = ::onUserSelected,
            onBackClick = ::navigateUp
        )
    }
}

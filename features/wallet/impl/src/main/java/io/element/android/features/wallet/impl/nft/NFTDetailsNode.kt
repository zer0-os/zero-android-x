/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.nft

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.wallet.api.WalletNFTDetailsEntryPoint
import io.element.android.features.wallet.impl.nft.view.NFTDetailsView
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
class NFTDetailsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: NFTDetailsPresenter.Factory,
) : Node(buildContext, plugins = plugins) {

    private val inputs = inputs<WalletNFTDetailsEntryPoint.Params>()
    private val presenter = presenterFactory.create(inputs.nft)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()

        NFTDetailsView(
            modifier = modifier,
            state = state,
            onBackClick = ::navigateUp
        )
    }
}

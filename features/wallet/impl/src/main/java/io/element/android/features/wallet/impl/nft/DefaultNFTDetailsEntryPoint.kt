/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.nft

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.features.wallet.api.WalletNFTDetailsEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
@Inject
class DefaultNFTDetailsEntryPoint : WalletNFTDetailsEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): WalletNFTDetailsEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : WalletNFTDetailsEntryPoint.NodeBuilder {
            override fun params(params: WalletNFTDetailsEntryPoint.Params): WalletNFTDetailsEntryPoint.NodeBuilder {
                plugins += params
                return this
            }

            override fun build(): Node {
                // Set node based on initial target
                return parentNode.createNode<NFTDetailsNode>(buildContext, plugins)
            }
        }
    }
}

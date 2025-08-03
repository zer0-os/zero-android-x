/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.features.wallettransactions.api.WalletTransactionsEntryPoint
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenNode
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.AppScope
import javax.inject.Inject

@ContributesBinding(AppScope::class)
class DefaultWalletTransactionsEntryPoint @Inject constructor() : WalletTransactionsEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): WalletTransactionsEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : WalletTransactionsEntryPoint.NodeBuilder {
            override fun params(params: WalletTransactionsEntryPoint.Params): WalletTransactionsEntryPoint.NodeBuilder {
                plugins += params
                return this
            }

            override fun callback(callback: WalletTransactionsEntryPoint.Callback): WalletTransactionsEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                // Set node based on initial target
                return parentNode.createNode<TransferTokenNode>(buildContext, plugins)
            }
        }
    }
}

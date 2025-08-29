/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.features.wallet.api.ManageWalletsEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.AppScope
import javax.inject.Inject

@ContributesBinding(AppScope::class)
class DefaultManageWalletsEntryPoint @Inject constructor() : ManageWalletsEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): ManageWalletsEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : ManageWalletsEntryPoint.NodeBuilder {
            override fun callback(callback: ManageWalletsEntryPoint.Callback): ManageWalletsEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                // Set node based on initial target
                return parentNode.createNode<ManageWalletsNode>(buildContext, plugins)
            }
        }
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.features.createfeed.api.CreateFeedEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.AppScope
import javax.inject.Inject

@ContributesBinding(AppScope::class)
class DefaultCreateFeedEntryPoint @Inject constructor() : CreateFeedEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): CreateFeedEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : CreateFeedEntryPoint.NodeBuilder {
            override fun callback(callback: CreateFeedEntryPoint.Callback): CreateFeedEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<CreateFeedNode>(buildContext, plugins)
            }
        }
    }
}

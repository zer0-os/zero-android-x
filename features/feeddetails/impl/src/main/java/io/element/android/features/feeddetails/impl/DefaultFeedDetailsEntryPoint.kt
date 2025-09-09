/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.features.feeddetails.api.FeedDetailsEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
@Inject
class DefaultFeedDetailsEntryPoint: FeedDetailsEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): FeedDetailsEntryPoint.NodeBuilder {
        return object : FeedDetailsEntryPoint.NodeBuilder {
            val plugins = ArrayList<Plugin>()

            override fun params(params: FeedDetailsEntryPoint.Params): FeedDetailsEntryPoint.NodeBuilder {
                plugins += params
                return this
            }

            override fun callback(callback: FeedDetailsEntryPoint.Callback): FeedDetailsEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<FeedDetailsNode>(buildContext, plugins)
            }
        }
    }
}

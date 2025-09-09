/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.features.feeduserprofile.api.FeedUserProfileEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
@Inject
class DefaultFeedUserProfileEntryPoint: FeedUserProfileEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): FeedUserProfileEntryPoint.NodeBuilder {
        return object : FeedUserProfileEntryPoint.NodeBuilder {
            val plugins = ArrayList<Plugin>()

            override fun params(params: FeedUserProfileEntryPoint.Params): FeedUserProfileEntryPoint.NodeBuilder {
                plugins += params
                return this
            }

            override fun callback(callback: FeedUserProfileEntryPoint.Callback): FeedUserProfileEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<FeedUserProfileNode>(buildContext, plugins)
            }
        }
    }
}

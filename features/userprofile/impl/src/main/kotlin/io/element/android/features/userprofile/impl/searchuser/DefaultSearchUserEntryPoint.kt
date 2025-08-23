/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.searchuser

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.features.userprofile.api.SearchUserEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.AppScope
import javax.inject.Inject

@ContributesBinding(AppScope::class)
class DefaultSearchUserEntryPoint @Inject constructor() : SearchUserEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): SearchUserEntryPoint.NodeBuilder {
        return object : SearchUserEntryPoint.NodeBuilder {
            val plugins = ArrayList<Plugin>()

            override fun callback(callback: SearchUserEntryPoint.Callback): SearchUserEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<SearchUserNode>(buildContext, plugins)
            }
        }
    }
}

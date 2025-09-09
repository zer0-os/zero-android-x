package io.element.android.features.zeroinvite.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.features.zeroinvite.api.InviteEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
@Inject
class DefaultInviteEntryPoint : InviteEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): InviteEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : InviteEntryPoint.NodeBuilder {
            override fun callback(callback: InviteEntryPoint.Callback): InviteEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<InviteNode>(buildContext, plugins)
            }
        }
    }
}

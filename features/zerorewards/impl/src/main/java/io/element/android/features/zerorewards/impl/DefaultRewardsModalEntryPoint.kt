package io.element.android.features.zerorewards.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.element.android.features.zerorewards.api.RewardsModalEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
@Inject
class DefaultRewardsModalEntryPoint: RewardsModalEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): RewardsModalEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : RewardsModalEntryPoint.NodeBuilder {
            override fun callback(callback: RewardsModalEntryPoint.Callback): RewardsModalEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<RewardsModalNode>(buildContext, plugins)
            }
        }
    }
}

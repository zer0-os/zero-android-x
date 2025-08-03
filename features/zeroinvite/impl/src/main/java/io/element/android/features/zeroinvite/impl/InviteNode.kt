package io.element.android.features.zeroinvite.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.anvilannotations.ContributesNode
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
class InviteNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: InvitePresenter,
) : Node(buildContext, plugins = plugins) {

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()

        MessengerInviteView(
            modifier = modifier,
            state = state,
            onBackClick = ::navigateUp
        )
    }
}

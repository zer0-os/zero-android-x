package io.element.android.features.zerorewards.impl

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.anvilannotations.ContributesNode
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
class RewardsModalNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: RewardsModalPresenter,
) : Node(buildContext, plugins = plugins) {

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val activity = LocalContext.current as Activity

        RewardsModalView(
            modifier = modifier,
            state = state,
            onBackClick = ::navigateUp
        )
    }
}

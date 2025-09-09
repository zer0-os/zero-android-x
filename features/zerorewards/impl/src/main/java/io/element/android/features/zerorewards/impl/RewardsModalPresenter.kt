package io.element.android.features.zerorewards.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient

@Inject
class RewardsModalPresenter(
    private val client: MatrixClient,
) : Presenter<RewardsModalState> {
    @Composable
    override fun present(): RewardsModalState {
        val coroutineScope = rememberCoroutineScope()
        val userRewards = client.userRewards.collectAsState()

        LaunchedEffect(Unit) {
            // Fetch user rewards
            client.getUserRewards()
        }

        return RewardsModalState(userRewards = userRewards.value)
    }
}

package io.element.android.features.zerorewards.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Composable
fun RewardsModalView(
    modifier: Modifier = Modifier,
    state: RewardsModalState
) {

}

@PreviewsDayNight
@Composable
fun RewardsModalViewPreview() = ElementPreview {
    RewardsModalView(state = RewardsModalState(userRewards = ZeroUserRewards.empty()))
}

package io.element.android.support.zero.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

@Composable
fun ZeroOnboardingView(
    onSignIn: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onSignIn.invoke()
    }

    Box(modifier = Modifier.fillMaxSize())
}

@PreviewsDayNight
@Composable
fun ZeroOnboardingViewPreview() = ElementPreview {
    ZeroOnboardingView()
}

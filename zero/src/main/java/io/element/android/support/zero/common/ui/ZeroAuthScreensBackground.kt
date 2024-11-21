package io.element.android.support.zero.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.component.BlurredBackground
import io.element.android.support.zero.common.ui.component.OverlappingLoadingContainer

@Composable
fun ZeroAuthScreensBackground(
    isLoading: Boolean = false,
    content: @Composable () -> Unit = {}
) {
	Box {
		BlurredBackground(background = R.drawable.bg_auth)
		OverlappingLoadingContainer(
			loading = isLoading,
			modifier = Modifier.fillMaxSize().background(Color.Transparent)
		) {
			content()
		}
	}
}

@PreviewsDayNight
@Composable
fun ZeroAuthScreensBackgroundPreview() = ElementTheme {
    ZeroAuthScreensBackground()
}

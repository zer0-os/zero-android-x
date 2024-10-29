package io.element.android.support.zero.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.component.OverlappingLoadingContainer

@Composable
fun ZeroOnboardingViewBackground(isLoading: Boolean = false, content: @Composable () -> Unit) {
	Box {
		Image(
			modifier = Modifier.fillMaxSize(),
			painter = painterResource(R.drawable.img_bg_landing_invite),
			contentDescription = null,
			contentScale = ContentScale.Crop
		)
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
fun ZeroOnboardingViewBackgroundPreview() = ElementPreview {
    ZeroOnboardingViewBackground(content = {})
}

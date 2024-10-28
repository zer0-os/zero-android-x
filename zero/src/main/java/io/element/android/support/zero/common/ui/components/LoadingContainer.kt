package io.element.android.support.zero.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

@Composable
fun LoadingContainer(
	modifier: Modifier = Modifier,
	loading: Boolean,
	size: Dp = 42.dp,
	content: @Composable () -> Unit
) {
	if (loading) {
		Column(
			modifier = modifier.fillMaxSize().defaultMinSize(minHeight = 100.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularProgress(size = size)
		}
	} else {
		content()
	}
}

@Composable
fun OverlappingLoadingContainer(
	modifier: Modifier = Modifier,
	loading: Boolean,
	size: Dp = 42.dp,
	bgColor: Color = Color.Black.copy(alpha = 0.65f),
	content: @Composable () -> Unit
) {
	Box(modifier, contentAlignment = Alignment.Center) {
		content()
		if (loading) {
			Column(
				modifier =
				modifier
					.fillMaxSize()
					.clickable {}
					.defaultMinSize(minHeight = 100.dp)
					.background(color = bgColor),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				CircularProgress(size = size)
			}
		}
	}
}

@PreviewsDayNight
@Composable
fun LoadingContainerPreview() = ElementPreview {
	OverlappingLoadingContainer(loading = true) {
        Text(text = "Testing")
    }
}

package io.element.android.support.zero.common.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R

@Composable
fun BlurredBackground(
	modifier: Modifier = Modifier,
	@DrawableRes background: Int = R.drawable.bg_level_3
) {
	Image(
		modifier = modifier.fillMaxWidth(),
		painter = painterResource(background),
		contentDescription = null,
		contentScale = ContentScale.Crop
	)
}

@Composable
fun SheetBlurredBackground(
	modifier: Modifier = Modifier,
	@DrawableRes background: Int = R.drawable.bg_level_3
) {
	Image(
		modifier = modifier,
		painter = painterResource(background),
		contentDescription = null,
		contentScale = ContentScale.Crop
	)
}

@PreviewsDayNight @Composable
fun BlurredBackgroundPreview() = ElementPreview {
    BlurredBackground()
}

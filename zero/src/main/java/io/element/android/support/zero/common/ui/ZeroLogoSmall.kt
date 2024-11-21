package io.element.android.support.zero.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R

@Composable
fun ZeroLogoSmall(
	modifier: Modifier = Modifier.wrapContentSize(),
	contentScale: ContentScale = ContentScale.Fit
) {
	Image(
		modifier = modifier,
		painter = painterResource(R.drawable.zero_logo_icon_small),
		contentDescription = null,
		contentScale = contentScale
	)
}

@PreviewsDayNight @Composable
fun ZeroLogoSmallPreview() = ElementPreview {
    ZeroLogoSmall()
}

package io.element.android.support.zero.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R

@Composable
fun ZeroLogo(modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
	Image(
		modifier = modifier.width(233.dp).height(120.dp),
		painter = painterResource(R.drawable.zero_logo),
		contentDescription = "cd_zero_logo",
		contentScale = contentScale
	)
}

@PreviewsDayNight
@Composable
fun ZeroLogoPreview() = ElementPreview {
    ZeroLogo()
}

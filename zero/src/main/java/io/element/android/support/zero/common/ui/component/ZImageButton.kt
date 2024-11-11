package io.element.android.support.zero.common.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.R

@Composable
fun ZImageButton(
	modifier: Modifier = Modifier,
	@DrawableRes image: Int,
	text: String,
	enabled: Boolean = true,
	onClick: () -> Unit = {}
) {
	if (enabled) {
		Card(
			modifier = modifier.wrapContentWidth(),
			onClick = onClick,
			shape = RoundedCornerShape(75.dp)
		) {
			Image(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				painter = painterResource(id = image),
				contentDescription = null
			)
		}
	} else {
		Text(
			modifier = modifier,
			text = text,
			style = ElementTheme.zeroTypography.fontBodyLgMedium,
			color = Color.DarkGray
		)
	}
}

@PreviewsDayNight
@Composable
fun ZImageButtonPreview() = ElementPreview {
    ZImageButton(
        image = R.drawable.img_btn_connect_wallet,
        text = ""
    )
}

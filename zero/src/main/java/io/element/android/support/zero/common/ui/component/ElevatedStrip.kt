package io.element.android.support.zero.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.common.ui.theme.PADDING_4X

private val DEFAULT_CONTAINER_COLORS =
	listOf(
		Color(0xDF0A0A0A),
		Color(0xDF0A0A0A),
		Color(0xEF0A0A0A),
		Color(0xEF0A0A0A),
		Color(0xFF0A0A0A),
		Color(0xFF0A0A0A),
		Color(0xFF0A0A0A)
	)

private val DEFAULT_STOKE_COLORS =
	listOf(
		Color(0x05FFFFFF),
		Color(0x20FFFFFF),
		Color(0x20FFFFFF),
		Color(0x10FFFFFF),
		Color(0x05FFFFFF),
		Color(0x05FFFFFF)
	)

@Composable
fun ElevatedStrip(
	modifier: Modifier = Modifier.fillMaxWidth(),
	parentSpacing: Dp = PADDING_4X.dp,
	elevation: Dp = 12.dp,
	shape: Shape = RoundedCornerShape(50.dp),
	strokeColors: List<Color> = DEFAULT_STOKE_COLORS,
	containerColors: List<Color> = DEFAULT_CONTAINER_COLORS,
	strokePadding: Dp = 1.dp,
	content: @Composable BoxScope.() -> Unit = {}
) {
	ElevatedCard(
		modifier =
		modifier.padding(parentSpacing).graphicsLayer { this.shadowElevation = elevation.toPx() },
		shape = shape,
		elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
	) {
		Box {
			val boxModifier =
				modifier.background(
					shape = shape,
					brush = Brush.horizontalGradient(colors = strokeColors)
				)
			Box(modifier = boxModifier)
			Box(
				modifier =
				Modifier.padding(strokePadding)
					.background(
						shape = shape,
						brush = Brush.horizontalGradient(colors = containerColors)
					)
			) {
				content(this)
			}
		}
	}
}

@PreviewsDayNight @Composable
fun ElevatedStripPreview() = ElementPreview {
    ElevatedStrip()
}

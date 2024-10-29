package io.element.android.support.zero.common.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.theme.PADDING_5X
import io.element.android.support.zero.common.ui.theme.SPACING_2X

@Composable
fun ElevatedButton(
    modifier: Modifier = Modifier,
    text: String = "",
    enabled: Boolean = true,
    drawableStart: ImageVector? = null,
    drawableEnd: ImageVector? = null,
    elevation: Dp = 12.dp,
    defaultContentColor: Color = ElementTheme.colors.bgAccentHovered,
    onClick: () -> Unit = {}
) {
    val contentColor = if (enabled) defaultContentColor else Color.DarkGray
    val boxModifier =
        if (enabled) {
            modifier.clickable { onClick() }
        } else modifier

    if (enabled) {
        Card(
            modifier = boxModifier.graphicsLayer { this.shadowElevation = elevation.toPx() },
            shape = RoundedCornerShape(75.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Box(
                modifier = boxModifier.width(IntrinsicSize.Max),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.bg_img_button),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = PADDING_5X.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    drawableStart?.let {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = it,
                            contentDescription = null,
                            tint = contentColor
                        )
                        if (text.isNotBlank()) {
                            Spacer(modifier = Modifier.size(SPACING_2X.dp))
                        }
                    }
                    if (text.isNotBlank()) {
                        Text(
                            text = text,
                            style = ElementTheme.typography.fontBodyLgMedium,
                            color = contentColor
                        )
                    }
                    drawableEnd?.let {
                        if (text.isNotBlank()) {
                            Spacer(modifier = Modifier.size(SPACING_2X.dp))
                        }
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = it,
                            contentDescription = null,
                            tint = contentColor
                        )
                    }
                }
            }
        }
    } else {
        Text(text = text, style = ElementTheme.typography.fontBodyLgMedium, color = contentColor)
    }
}

@PreviewsDayNight
@Composable
fun ElevatedButtonPreview() = ElementPreview {
    ElevatedButton(text = "Button", drawableEnd = Icons.AutoMirrored.Filled.ArrowForward)
}

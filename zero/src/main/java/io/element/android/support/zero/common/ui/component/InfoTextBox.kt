package io.element.android.support.zero.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.support.zero.common.ui.theme.PADDING_2X
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_2X

@Composable
fun ErrorTextBox(modifier: Modifier = Modifier, text: String) {
    InfoTextBox(modifier = modifier, type = InfoTextType.ERROR, text = text)
}

@Composable
fun SuccessTextBox(modifier: Modifier = Modifier, text: String) {
    InfoTextBox(modifier = modifier, type = InfoTextType.SUCCESS, text = text)
}

@Composable
fun InfoBox(modifier: Modifier = Modifier, text: String) {
    InfoTextBox(modifier = modifier, type = InfoTextType.OTHER, text = text)
}

@Composable
private fun InfoTextBox(
    modifier: Modifier = Modifier,
    type: InfoTextType = InfoTextType.OTHER,
    text: String
) {
    val contentColor: Color =
        when (type) {
            InfoTextType.SUCCESS -> Color.Green
            InfoTextType.ERROR -> Color.Red
            else -> Color.Gray
        }
    val backgroundColor =
        when (type) {
            InfoTextType.SUCCESS -> Color(0xFF0C1F17)
            InfoTextType.ERROR -> Color(0xFF291415)
            else -> Color(0x10A3A2A3)
        }
    Box(modifier = modifier.background(color = backgroundColor, shape = RoundedCornerShape(6.dp))) {
        Row(
            modifier = Modifier.padding(vertical = PADDING_2X.dp, horizontal = PADDING_4X.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector =
                when (type) {
                    InfoTextType.SUCCESS -> Icons.Default.Check
                    InfoTextType.ERROR -> Icons.Default.Error
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            Text(
                text = text,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = contentColor
            )
        }
    }
}

private enum class InfoTextType {
    SUCCESS,
    ERROR,
    OTHER
}

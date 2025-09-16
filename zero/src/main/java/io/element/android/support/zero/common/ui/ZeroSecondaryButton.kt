/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun ZeroSecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier
            .height(55.dp)
            .border(
                width = 1.dp,
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(11.dp)
            ),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        val textBuilderString = buildString {
            append(text)
            if (icon != null && text.isNotBlank()) {
                append(" ")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = textBuilderString,
                style = ElementTheme.typography.fontBodyMdMedium
            )
            if (icon != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = icon,
                    contentDescription = "logo_icon",
                    tint = ElementTheme.colors.zeroBrandColor
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun ZeroSecondaryButtonPreview() = ElementPreview {
    ZeroSecondaryButton(text = "Secondary Button")
}

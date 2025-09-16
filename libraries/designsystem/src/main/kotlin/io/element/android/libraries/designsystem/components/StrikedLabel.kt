/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Text

@Composable
fun StrikedLabel(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = ElementTheme.colors.textSecondary,
    textStyle: TextStyle = ElementTheme.typography.fontBodySmRegular,
    strikeColor: Color = ElementTheme.colors.textSecondary
) {
    Box(modifier) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.Center),
            color = strikeColor
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .background(ElementTheme.colors.bgCanvasDefault)
                .padding(horizontal = 12.dp),
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}

@PreviewsDayNight
@Composable
internal fun StrikedLabelPreview() = ElementPreview {
    StrikedLabel(
        text = "Label",
        textColor = Color.Gray,
        textStyle = ElementTheme.typography.fontBodyLgMedium,
        strikeColor = Color.Gray
    )
}

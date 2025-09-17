/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun OnboardingScreenHeader(
    title: String,
    subTitle: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            style = ElementTheme.zeroTypography.fontHeadingLgRegular,
            color = ElementTheme.colors.zeroBrandColor
        )
        Spacer(modifier = Modifier.size(8.dp))
        if (subTitle != null) {
            Text(
                text = subTitle,
                style = ElementTheme.zeroTypography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun OnboardingScreenHeaderPreview() = ElementPreview {
    OnboardingScreenHeader(
        title = "Header Title",
        subTitle = "This is the subtitle"
    )
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun AppMaintenanceModeView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgCanvasDefault)
            .imePadding()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(io.element.android.support.zero.R.drawable.img_app_maintenance),
                contentDescription = null
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "Under Maintenance",
                style = ElementTheme.typography.fontHeadingMdBold,
                color = ElementTheme.colors.zeroBrandColor
            )
            Spacer(Modifier.size(2.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "The app is under maintenance. We’ll be back soon!",
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun AppMaintenanceModeViewPreview() = ElementPreview {
    AppMaintenanceModeView()
}

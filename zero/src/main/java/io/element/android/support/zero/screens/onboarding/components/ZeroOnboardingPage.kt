/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.screens.onboarding.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.support.zero.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroOnboardingPage(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    appbarActions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.home_zero_title),
                    contentDescription = "onboarding_header",
                )
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            navigationIcon = {
                if (showBackButton) {
                    BackButton(onClick = onBackClick)
                }
            },
            actions = appbarActions,
            windowInsets = WindowInsets(0.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        footer()
    }
}

@PreviewsDayNight
@Composable
fun ZeroOnboardingPagePreview() = ElementPreview {
    ZeroOnboardingPage()
}

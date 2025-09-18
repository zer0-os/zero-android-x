/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.support.zero.R

@Composable
fun ZeroAuthSegmentControl(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        ImageVector.vectorResource(R.drawable.ic_logo_walletconnect),
        ImageVector.vectorResource(R.drawable.ic_logo_email),
    )

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.iconSecondary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            tabs.forEachIndexed { index, icon ->
                val isSelected = selectedTab == index
                val borderColor = if (isSelected) ElementTheme.colors.zeroBrandColor else Color.Transparent
                val backgroundColor = if (isSelected) ElementTheme.colors.zeroBrandColor.copy(alpha = 0.15f) else Color.Transparent
                val iconTint = if (isSelected) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.iconSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun ZeroAuthSegmentControlPreview() = ElementPreview {
    ZeroAuthSegmentControl(
        selectedTab = 0,
        onTabSelected = {}
    )
}

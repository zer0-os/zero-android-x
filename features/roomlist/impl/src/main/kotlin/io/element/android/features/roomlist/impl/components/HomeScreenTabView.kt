/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.roomlist.impl.model.HomeScreenTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun HomeScreenTabView(
    selectedNavigationTab: HomeScreenTab = HomeScreenTab.CHAT,
    onTabSelected: (HomeScreenTab) -> Unit = {}
) {
    NavigationBar(
        containerColor = Color(0xFF1A1B1F)
    ) {
        HomeScreenTab.entries.forEachIndexed { _, homeScreenTab ->
            NavigationBarItem(
                selected = selectedNavigationTab == homeScreenTab,
                onClick = {
                    onTabSelected(homeScreenTab)
                },
                icon = {
                    Icon(imageVector = ImageVector.vectorResource(homeScreenTab.icon),
                        contentDescription = homeScreenTab.title)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedIconColor = ElementTheme.colors.zeroBrandColor,
                    selectedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun HomeScreenTabViewPreview() = ElementPreview {
    HomeScreenTabView()
}

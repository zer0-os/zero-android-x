/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun HomeScreenTabView(
    modifier: Modifier = Modifier,
    selectedNavigationTab: HomeScreenTab = HomeScreenTab.CHAT,
    onTabSelected: (HomeScreenTab) -> Unit = {}
) {
    ZeroStyledNavigationTabView(
        modifier = modifier,
        selectedNavigationTab = selectedNavigationTab,
        onTabSelected = onTabSelected
    )
}

@Composable
fun ZeroStyledNavigationTabView(
    modifier: Modifier = Modifier,
    selectedNavigationTab: HomeScreenTab = HomeScreenTab.CHAT,
    onTabSelected: (HomeScreenTab) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults
            .cardColors()
            .copy(containerColor = Color(0xFF1A1B1F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HomeScreenTab.entries.forEach { tab ->
                val isSelected = selectedNavigationTab == tab
                IconButton(onClick = { onTabSelected(tab) }) {
                    Icon(
                        imageVector = if (isSelected) {
                            ImageVector.vectorResource(tab.selectedIcon)
                        } else {
                            ImageVector.vectorResource(tab.icon)
                        },
                        contentDescription = tab.title,
                        tint = if (isSelected) {
                            ElementTheme.colors.zeroBrandColor
                        } else NavigationBarItemDefaults.colors().unselectedTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultNavigationTabView(
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
                    Icon(
                        imageVector = ImageVector.vectorResource(homeScreenTab.icon),
                        contentDescription = homeScreenTab.title
                    )
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

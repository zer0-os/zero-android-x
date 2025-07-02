/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.roomlist.impl.model.FeedsScreenTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun FeedsScreenTabView(
    selectedTab: FeedsScreenTab = FeedsScreenTab.FOLLOWING,
    onTabSelected: (FeedsScreenTab) -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = ElementTheme.colors.zeroBrandColor
            )
        },
        divider = { }
    ) {
        FeedsScreenTab.entries.forEachIndexed { index, tab ->
            Tab(
                text = { Text(tab.title) },
                selected = selectedTabIndex == index,
                selectedContentColor = ElementTheme.colors.zeroBrandColor,
                unselectedContentColor = ElementTheme.colors.textSecondary,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected.invoke(tab)
                }
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun FeedsScreenTabPreview() = ElementPreview {
    FeedsScreenTabView()
}

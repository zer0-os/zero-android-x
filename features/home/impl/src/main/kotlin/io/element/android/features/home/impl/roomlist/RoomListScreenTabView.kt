/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.roomlist

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.model.ChatScreenTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun RoomListScreenTabView(
    selectedTab: ChatScreenTab = ChatScreenTab.ALL,
    onTabSelected: (ChatScreenTab) -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(selectedTab.ordinal) }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = ElementTheme.colors.zeroBrandColor
            )
        },
        divider = { }
    ) {
        ChatScreenTab.entries.forEachIndexed { index, tab ->
            Tab(
                text = {
                    Text(
                        tab.title,
                        style = ElementTheme.zeroTypography.fontBodyMdMedium
                    )
                },
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
internal fun RoomListScreenTabViewPreview() = ElementPreview {
    RoomListScreenTabView()
}

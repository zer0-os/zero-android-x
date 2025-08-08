/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.model.WalletContentTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun WalletContentTabView(
    modifier: Modifier = Modifier,
    selectedTab: WalletContentTab = WalletContentTab.TOKENS,
    onTabSelected: (WalletContentTab) -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(selectedTab.ordinal) }

    Box(modifier = modifier) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ElementTheme.colors.zeroBrandColor
                )
            },
            divider = {}
        ) {
            WalletContentTab.entries.forEachIndexed { index, tab ->
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
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@PreviewsDayNight
@Composable
internal fun WalletContentTabViewPreview() = ElementPreview {
    WalletContentTabView()
}

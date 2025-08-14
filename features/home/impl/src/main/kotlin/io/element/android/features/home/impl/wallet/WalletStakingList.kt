/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

@Composable
fun WalletStakingList(
    state: WalletContentState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // Header Row
        item {
            Row(modifier = Modifier.padding(8.dp)) {
                StakingContentHeading(
                    modifier = Modifier.weight(1f),
                    text = "Pool Name"
                )
                StakingContentHeading(
                    modifier = Modifier.weight(0.5f),
                    text = "TVL"
                )
                StakingContentHeading(
                    modifier = Modifier.weight(0.5f),
                    text = "Your Stake"
                )
            }
        }
    }
}

@Composable
fun StakingContentHeading(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = ElementTheme.typography.fontBodyMdMedium,
        color = ElementTheme.colors.textSecondary
    )
}

@PreviewsDayNight
@Composable
fun WalletStakingListPreview(
    @PreviewParameter(WalletContentStateProvider::class) state: WalletContentState
) = ElementPreview {
    WalletStakingList(state = state)
}

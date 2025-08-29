/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageWalletsView(
    modifier: Modifier = Modifier,
    state: ManageWalletsState,
    onBackClick: () -> Unit = {}
) {
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = "Wallets"
    ) {

    }
}

@PreviewsDayNight
@Composable
fun ManageWalletsViewPreview(
    @PreviewParameter(ManageWalletsStateProvider::class) state: ManageWalletsState
) = ElementPreview {
    ManageWalletsView(state = state)
}

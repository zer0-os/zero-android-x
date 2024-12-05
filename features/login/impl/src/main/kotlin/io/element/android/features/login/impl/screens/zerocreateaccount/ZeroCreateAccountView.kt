/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun ZeroCreateAccountView(
    modifier: Modifier = Modifier,
    state: ZeroCreateAccountState,
    onBackClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(state.inviteCode,
            modifier = Modifier.align(Alignment.Center),
            style = ElementTheme.zeroTypography.fontHeadingMdBold,
            color = ElementTheme.colors.textPrimary)
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken.confirmation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenFlowStep
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun CompletedTransferView(
    state: TransferTokenState
) {
    val isSuccess = state.flowStep == TransferTokenFlowStep.COMPLETED
    val text = if (isSuccess) "Success" else "Failure"
    val color = if (isSuccess) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.textCriticalPrimary
    val icon = if (isSuccess) CompoundIcons.Check() else CompoundIcons.Close()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = text,
                style = ElementTheme.typography.fontHeadingMdBold,
                color = color
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun CompletedTransferViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    CompletedTransferView(state = state)
}

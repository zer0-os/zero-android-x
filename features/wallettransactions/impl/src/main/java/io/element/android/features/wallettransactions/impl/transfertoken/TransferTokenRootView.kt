/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallettransactions.impl.transfertoken.confirmation.CompletedTransferView
import io.element.android.features.wallettransactions.impl.transfertoken.confirmation.ConfirmTransferView
import io.element.android.features.wallettransactions.impl.transfertoken.confirmation.TransactionInProgressView
import io.element.android.features.wallettransactions.impl.transfertoken.recipient.SelectRecipientView
import io.element.android.features.wallettransactions.impl.transfertoken.token.SelectTokenView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Scaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferTokenRootView(
    modifier: Modifier = Modifier,
    state: TransferTokenState,
    onBackClick: () -> Unit = {}
) {
    val topBarText: () -> String = {
        when (state.flowStep) {
            TransferTokenFlowStep.TOKEN -> "Select Asset"
            TransferTokenFlowStep.COMPLETED -> "Sent"
            else -> "Send"
        }
    }

    BackHandler { handleBackClick(state, onBackClick) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (state.flowStep != TransferTokenFlowStep.COMPLETED &&
                        state.flowStep != TransferTokenFlowStep.ERROR &&
                        state.flowStep != TransferTokenFlowStep.IN_PROGRESS) {
                        BackButton(onClick = { handleBackClick(state, onBackClick) })
                    }
                },
                title = { Text(topBarText(), style = ElementTheme.typography.fontBodyLgMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
                    .copy(containerColor = Color.Black),
                actions = {
                    if (state.flowStep == TransferTokenFlowStep.COMPLETED ||
                        state.flowStep == TransferTokenFlowStep.ERROR) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = CompoundIcons.Close(), contentDescription = null, tint = ElementTheme.colors.textPrimary)
                        }
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .consumeWindowInsets(it)
                    .padding(16.dp)
            ) {
                when (state.flowStep) {
                    TransferTokenFlowStep.RECIPIENT -> SelectRecipientView(state = state)
                    TransferTokenFlowStep.TOKEN -> SelectTokenView(state = state)
                    TransferTokenFlowStep.CONFIRMATION -> ConfirmTransferView(state = state)
                    TransferTokenFlowStep.IN_PROGRESS -> TransactionInProgressView()
                    TransferTokenFlowStep.COMPLETED,
                    TransferTokenFlowStep.ERROR -> CompletedTransferView(state = state, onClose = onBackClick)
                }
            }
        }
    )
}

private fun handleBackClick(state: TransferTokenState, onRootBackClick: () -> Unit) {
    when (state.flowStep) {
        TransferTokenFlowStep.TOKEN -> state.eventSink(TransferTokenEvents.ToState(TransferTokenFlowStep.RECIPIENT))
        TransferTokenFlowStep.CONFIRMATION -> state.eventSink(TransferTokenEvents.ToState(TransferTokenFlowStep.TOKEN))
        TransferTokenFlowStep.IN_PROGRESS -> {
            //not allowed
        }
        else -> onRootBackClick()
    }
}

@PreviewsDayNight
@Composable
fun TransferTokenRootViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    TransferTokenRootView(state = state)
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.reown.appkit.ui.components.internal.AppKitComponent
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallet.impl.manage.ManageWalletUserAction
import io.element.android.features.wallet.impl.manage.ManageWalletsEvents
import io.element.android.features.wallet.impl.manage.ManageWalletsState
import io.element.android.features.wallet.impl.manage.ManageWalletsStateProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.preferences.PreferenceCategory
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.theme.PADDING_4X

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageWalletsView(
    modifier: Modifier = Modifier,
    state: ManageWalletsState,
    onBackClick: () -> Unit = {}
) {
    Box(modifier = modifier
        .fillMaxSize()
        .imePadding()
        .navigationBarsPadding()
    ) {
        if (state.actionState is AsyncAction.Success) {
            PreferencePage(
                modifier = modifier,
                onBackClick = onBackClick,
                title = "Wallets"
            ) {
                val headerCount = if (state.selfCustodyWalletsCount > 0) {
                    state.selfCustodyWalletsCount.toString()
                } else {
                    "No"
                }
                PreferenceCategory(
                    title = "$headerCount Self-Custody Wallet".uppercase(),
                    showTopDivider = false
                ) {
                    WalletsListView(
                        modifier = Modifier
                            .height((state.selfCustodyWallets.count().times(80)).dp)
                            .padding(PADDING_4X.dp),
                        wallets = state.selfCustodyWallets,
                        canRemoveWallet = true,
                        onWalletTap = {},
                        onRemoveWallet = { wallet ->
                            state.eventSink(ManageWalletsEvents.RemoveWallet(wallet))
                        }
                    )
                }

                AddWalletButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { state.eventSink(ManageWalletsEvents.CheckAndLinkWallet) }
                )

                PreferenceCategory(
                    title = "Zero Wallet".uppercase(),
                    showTopDivider = false
                ) {
                    WalletsListView(
                        modifier = Modifier
                            .height((state.zeroWallets.count().times(80)).dp)
                            .padding(PADDING_4X.dp),
                        wallets = state.zeroWallets,
                        canRemoveWallet = false,
                        onWalletTap = { wallet ->
                            state.eventSink(ManageWalletsEvents.ShowWallet(wallet))
                        },
                        onRemoveWallet = {}
                    )
                }
            }
        }

        if (state.actionState is AsyncAction.Loading) {
            ProgressDialog()
        }

        if (state.actionState is AsyncAction.Failure) {
            ErrorDialog(
                content = state.actionState.error.message ?: stringResource(CommonStrings.error_unknown),
                onSubmit = { state.eventSink(ManageWalletsEvents.HideError) }
            )
        }

        when (state.userActionState) {
            ManageWalletUserAction.None -> {}
            ManageWalletUserAction.SelectWallet -> {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    AppKitComponent(
                        shouldOpenChooseNetwork = false,
                        closeModal = {
                            state.eventSink(ManageWalletsEvents.WalletLinkingCancelled)
                        }
                    )
                }
            }
            is ManageWalletUserAction.LinkWallet -> {
                if (state.firstSelfCustodyWallet != null) {
                    AlreadyConnectedWalletAlert(
                        walletAddress = state.firstSelfCustodyWallet!!.publicAddress,
                        onCancel = {
                            state.eventSink(ManageWalletsEvents.WalletLinkingCancelled)
                        }
                    )
                } else {
                    ConnectWalletAlert(
                        walletAddress = state.userActionState.address,
                        onConfirmLinking = { address, enableLoggingIn ->
                            state.eventSink(ManageWalletsEvents.ConfirmLinkWallet(address, enableLoggingIn))
                        },
                        onCancel = {
                            state.eventSink(ManageWalletsEvents.WalletLinkingCancelled)
                        }
                    )
                }
            }
            is ManageWalletUserAction.RemoveWallet -> {
                RemoveWalletAlert(
                    onConfirm = {
                        state.eventSink(ManageWalletsEvents.ConfirmDeleteWallet(state.userActionState.id))
                    },
                    onCancel = {
                        state.eventSink(ManageWalletsEvents.WalletLinkingCancelled)
                    }
                )
            }
        }
    }
}

@Composable
fun AddWalletButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CompoundIcons.Plus(),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(horizontal = 6.dp),
            text = "Add Wallet",
            style = ElementTheme.typography.fontBodyLgMedium,
            color = ElementTheme.colors.zeroBrandColor
        )
    }
}

@PreviewsDayNight
@Composable
fun ManageWalletsViewPreview(
    @PreviewParameter(ManageWalletsStateProvider::class) state: ManageWalletsState
) = ElementPreview {
    ManageWalletsView(state = state)
}

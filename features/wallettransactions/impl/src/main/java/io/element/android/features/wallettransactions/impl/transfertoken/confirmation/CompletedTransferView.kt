/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenEvents
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenFlowStep
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenStateProvider
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.displayName
import io.element.android.libraries.matrix.api.zero.wallet.isMeowToken
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.support.zero.common.ui.ZChainIcon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CompletedTransferView(
    state: TransferTokenState,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.token != null && state.meowPrice != null && state.transferAmount != null) {
            TokenInfoView(
                token = state.token,
                transferAmount = state.transferAmount,
                meowPrice = state.meowPrice
            )
        }

        if (state.recipient != null) {
            UsersInfoView(
                sender = state.currentUser,
                recipient = state.recipient
            )
        }

        if (state.transactionReceipt != null) {
            TransactionInfoView(
                transactionReceipt = state.transactionReceipt,
                isSuccess = state.flowStep == TransferTokenFlowStep.COMPLETED,
                onClose = onClose,
                viewTransaction = { transactionUrl ->
                    state.eventSink(TransferTokenEvents.ViewTransaction(transactionUrl))
                }
            )
        }
    }
}

@Composable
fun TokenInfoView(
    token: ZeroWalletToken,
    transferAmount: String,
    meowPrice: ZeroMeowPrice
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.5f),
                    spotColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.5f)
                )
                .background(Color.Black, CircleShape)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                    .clip(CircleShape),
                model = token.logo,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                contentDescription = null,
                placeholder = painterResource(io.element.android.libraries.designsystem.R.drawable.ic_zero_avatar_default)
            )
            ZChainIcon(modifier = Modifier.align(Alignment.BottomEnd).zIndex(1f))
        }

        Spacer(Modifier.size(16.dp))

        Text(
            text = "$transferAmount ${token.symbol.uppercase()}",
            style = ElementTheme.typography.fontHeadingSmMedium,
            color = ElementTheme.colors.textPrimary
        )

        val amount = transferAmount.toDoubleOrNull() ?: 0.0
        if (token.isMeowToken && amount > 0) {
            Spacer(Modifier.size(6.dp))
            Text(
                text = ZeroWalletUtil.getMeowTokenPriceFormatted(amount, meowPrice),
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary
            )
        }
    }
}

@Composable
fun UsersInfoView(
    sender: MatrixUser,
    recipient: ZeroWalletRecipient
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ElementTheme.colors.bgCanvasDefaultLevel1, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(
                avatarData = sender.getAvatarData(AvatarSize.UserListItem),
                avatarType = AvatarType.User
            )
            Spacer(Modifier.size(6.dp))
            sender.displayName?.let {
                Text(
                    text = it,
                    style = ElementTheme.typography.fontBodyLgMedium,
                    color = ElementTheme.colors.textPrimary
                )
            }
            ZeroWalletUtil.walletAddressDisplayText(sender.walletAddress)?.let {
                Text(
                    text = it,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .border(2.dp, ElementTheme.colors.bgCanvasDefaultLevel1, CircleShape)
                .size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(modifier = Modifier.offset(x = (-4).dp), imageVector = CompoundIcons.ChevronRight(), contentDescription = null)
            Icon(modifier = Modifier.offset(x = 4.dp), imageVector = CompoundIcons.ChevronRight(), contentDescription = null)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(
                avatarData = AvatarData(
                    id = recipient.userId,
                    name = recipient.name,
                    url = recipient.profileImage,
                    size = AvatarSize.UserListItem
                ),
                avatarType = AvatarType.User
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = recipient.displayName,
                style = ElementTheme.typography.fontBodyLgMedium,
                color = ElementTheme.colors.textPrimary
            )
            ZeroWalletUtil.walletAddressDisplayText(recipient.publicAddress)?.let {
                Text(
                    text = it,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun TransactionInfoView(
    transactionReceipt: ZeroWalletTransactionReceipt,
    isSuccess: Boolean,
    onClose: () -> Unit,
    viewTransaction: (String) -> Unit
) {
    val status = if (isSuccess) "Succeeded" else "Failed"
    val color = if (isSuccess) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.textCriticalPrimary
    val icon = if (isSuccess) CompoundIcons.Check() else CompoundIcons.Close()

    val date: () -> String = {
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy 'at' h:mm a", Locale.ENGLISH)
        LocalDateTime.now().format(formatter)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .border(2.dp, color, CircleShape)
                .padding(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = "Transaction $status",
            style = ElementTheme.typography.fontBodyLgMedium,
            color = color
        )
        Text(
            text = date(),
            style = ElementTheme.typography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(Modifier.size(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CloseButton(onClick = onClose)
            if (isSuccess) {
                Spacer(Modifier.size(12.dp))
                ViewTransactionButton(onClick = {
                    viewTransaction(transactionReceipt.blockExplorerUrl)
                })
            }
        }
    }
}

@Composable
fun RowScope.ViewTransactionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .weight(1f)
            .border(
                width = 1.dp,
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        Text(
            text = "View on ZScan",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = ElementTheme.colors.zeroBrandColor
        )
    }
}

@Composable
fun RowScope.CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.weight(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor
        ),
        onClick = onClick
    ) {
        Text(
            "Close",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@PreviewsDayNight
@Composable
fun CompletedTransferViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    CompletedTransferView(state = state, onClose = {})
}

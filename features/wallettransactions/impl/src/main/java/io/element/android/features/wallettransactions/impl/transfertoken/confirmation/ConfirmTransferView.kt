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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenEvents
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
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.zero.wallet.WalletChainsUtil
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.displayName
import io.element.android.support.zero.common.ui.AvaxChainIcon
import io.element.android.support.zero.common.ui.SwipeToConfirmButton
import io.element.android.support.zero.common.ui.ZChainIcon

@Composable
fun ConfirmTransferView(
    modifier: Modifier = Modifier,
    state: TransferTokenState
) {
    val recipient = state.recipient
    val token = state.token
    val transferAmount = state.transferAmount

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Confirm Transaction with",
                style = ElementTheme.zeroTypography.fontHeadingMdBold,
                color = ElementTheme.colors.textPrimary
            )

            Spacer(Modifier.size(32.dp))

            if (recipient != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(
                        avatarData = AvatarData(
                            id = recipient.userId,
                            name = recipient.name,
                            url = recipient.profileImage,
                            size = AvatarSize.DmCreationConfirmation
                        ),
                        avatarType = AvatarType.User
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = recipient.displayName,
                        style = ElementTheme.zeroTypography.fontHeadingSmMedium,
                        color = ElementTheme.colors.zeroBrandColor
                    )
                    Text(
                        text = recipient.publicAddress,
                        style = ElementTheme.typography.fontBodyMdRegular,
                        color = ElementTheme.colors.textSecondary
                    )
                }
                Spacer(Modifier.size(32.dp))
            }

            if (token != null && transferAmount != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElementTheme.colors.bgCanvasDefaultLevel1, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TokenView(token = token, amount = transferAmount)

                    Box(
                        modifier = Modifier
                            .border(2.dp, ElementTheme.colors.bgCanvasDefaultLevel1, CircleShape)
                            .size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(modifier = Modifier.offset(x = (-4).dp), imageVector = CompoundIcons.ChevronRight(), contentDescription = null)
                        Icon(modifier = Modifier.offset(x = 4.dp), imageVector = CompoundIcons.ChevronRight(), contentDescription = null)
                    }

                    TokenView(token = token, amount = transferAmount)
                }
            }
        }

        if (transferAmount != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Review the above before confirming.\nOnce made, your transaction is irreversible.",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.size(12.dp))
                SwipeToConfirmButton(
                    modifier = Modifier.padding(vertical = 8.dp),
                    onConfirm = {
                        state.eventSink(TransferTokenEvents.ConfirmTransaction)
                    })
            }
        }
    }
}

@Composable
fun TokenView(
    modifier: Modifier = Modifier,
    token: ZeroWalletToken,
    amount: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(60.dp)
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.bgCanvasDefault,
                    shape = CircleShape
                )
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(56.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                    .clip(CircleShape),
                model = token.logo,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                contentDescription = null,
                error = painterResource(io.element.android.libraries.designsystem.R.drawable.ic_zero_avatar_default)
            )
            if (WalletChainsUtil.isAvaxChain(token.chainId)) {
                AvaxChainIcon(modifier = Modifier.align(Alignment.BottomEnd).zIndex(1f))
            } else {
                ZChainIcon(modifier = Modifier.align(Alignment.BottomEnd).zIndex(1f))
            }
        }

        Spacer(Modifier.size(12.dp))

        Text(
            text = token.name,
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = amount,
            style = ElementTheme.zeroTypography.fontHeadingSmMedium,
            color = ElementTheme.colors.textSecondary
        )
    }
}

@PreviewsDayNight
@Composable
fun ConfirmTransferViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    ConfirmTransferView(state = state)
}

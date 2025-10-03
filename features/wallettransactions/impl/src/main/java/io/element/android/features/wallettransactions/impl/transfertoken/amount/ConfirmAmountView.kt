/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken.amount

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenEvents
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenStateProvider
import io.element.android.features.wallettransactions.impl.transfertoken.token.SelectedRecipientView
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.support.zero.common.util.wallet.WalletChainsUtil
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.common.ui.WalletChainIcon
import io.element.android.support.zero.common.ui.ZeroPrimaryButton

@Composable
fun ConfirmAmountView(
    modifier: Modifier = Modifier,
    state: TransferTokenState
) {
    val transferAmount = remember { mutableStateOf("0") }
    val isValidAmount: () -> Boolean = {
        (transferAmount.value.toDoubleOrNull() ?: 0.0) > 0
    }
    val sender = state.currentUser
    val recipient = state.recipient
    val token = state.token

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        if (token != null) {
            SenderView(sender, token, onAmountEntered = {
                if (it.isNotEmpty()) {
                    transferAmount.value = it
                } else {
                    transferAmount.value = "0"
                }
            })
        }

        Spacer(Modifier.size(20.dp))

        if (recipient != null && token != null) {
            ReceiverView(recipient, token, transferAmount.value)
        }

        Spacer(Modifier.weight(1f))

        if (isValidAmount()) {
            ZeroPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Continue",
                onClick = {
                    state.eventSink(TransferTokenEvents.ConfirmAmount(transferAmount.value))
                }
            )
        }
    }
}

@Composable
fun SenderView(
    currentUser: MatrixUser,
    token: ZeroWalletToken,
    onAmountEntered: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = ElementTheme.colors.bgCanvasDefaultLevel1, shape = RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ElementTheme.colors.bgCanvasDefault,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "From:",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
            currentUser.displayName?.let {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    text = it,
                    style = ElementTheme.typography.fontBodyLgMedium,
                    color = ElementTheme.colors.textPrimary
                )
            }
            ZeroWalletUtil.walletAddressDisplayText(currentUser.walletAddress)?.let {
                Text(
                    text = it,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }
        }
        TokenView(
            modifier = Modifier.padding(16.dp),
            token = token
        )
        TransferAmountInputField(
            modifier = Modifier,
            token = token,
            onAmountEntered = onAmountEntered
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TransferAmountInputField(
    modifier: Modifier = Modifier,
    token: ZeroWalletToken,
    onAmountEntered: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val amount = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val onAmountChanged: (String) -> Unit = {
        val tokenMaxAmount = token.amount.toDoubleOrNull() ?: 0.0
        val enteredAmount = it.toDoubleOrNull() ?: 0.0
        if (enteredAmount > tokenMaxAmount) {
            amount.value = token.amount
        } else {
            amount.value = it
        }
        onAmountEntered(amount.value)
    }

    val getBalance: () -> String = {
        val tokenMaxAmount = token.amount.toDoubleOrNull() ?: 0.0
        val enteredAmount = amount.value.toDoubleOrNull() ?: 0.0
        val balance = maxOf(0.0, tokenMaxAmount.minus(enteredAmount))
        String.format("%.2f", balance)
    }

    Column(horizontalAlignment = Alignment.Start) {
        Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                value = TextFieldValue(amount.value, selection = TextRange(amount.value.length)),
                onValueChange = { value ->
                    onAmountChanged(value.text)
                },
                placeholder = { Text("0", style = ElementTheme.typography.fontHeadingMdRegular) },
                singleLine = true,
                maxLines = 1,
                textStyle = ElementTheme.typography.fontHeadingMdRegular,
                shape = RectangleShape,
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            )
            if (amount.value != token.amount) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .background(
                            color = ElementTheme.colors.bgCanvasDefaultLevel1,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            onAmountEntered(token.amount)
                            amount.value = token.amount
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    text = "Use Max",
                    style = ElementTheme.typography.fontBodySmRegular
                )
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text("", modifier = Modifier.weight(1f))
            Text(
                "Balance: ${getBalance()}",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
        }
    }
}

@Composable
fun ReceiverView(
    recipient: ZeroWalletRecipient,
    token: ZeroWalletToken,
    transferAmount: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = ElementTheme.colors.bgCanvasDefaultLevel1, shape = RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            SelectedRecipientView(recipient)
        }
        TokenView(
            modifier = Modifier.padding(16.dp),
            token = token
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = transferAmount,
            style = ElementTheme.typography.fontHeadingMdRegular
        )
    }
}

@Composable
fun TokenView(
    modifier: Modifier = Modifier,
    token: ZeroWalletToken
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                error = painterResource(R.drawable.ic_zero_avatar_default)
            )
            val chain = WalletChainsUtil.getChain(token.chainId)
            if (chain != null) {
                WalletChainIcon(
                    modifier = Modifier.align(Alignment.BottomEnd).zIndex(1f),
                    icon = ImageVector.vectorResource(chain.logo)
                )
            }
        }

        Spacer(Modifier.size(12.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = token.name.uppercase(),
                style = ElementTheme.typography.fontHeadingSmRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = token.symbol,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun ConfirmAmountViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    ConfirmAmountView(state = state)
}

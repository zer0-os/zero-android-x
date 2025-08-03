/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken.recipient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenEvents
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenStateProvider
import io.element.android.features.wallettransactions.impl.transfertoken.WalletRecipientsListState
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.placeholderBackground
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil

@Composable
fun SelectRecipientView(
    state: TransferTokenState
) {
    Column {
        SearchRecipientTextField(
            onQueryChanged = { query ->
                state.eventSink(TransferTokenEvents.SearchRecipient(query))
            }
        )
        if (state.showRecipientsResult) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = "Results",
                style = ElementTheme.typography.fontBodyMdMedium,
                color = ElementTheme.colors.textSecondary
            )
        }
        when (state.recipientsListState) {
            WalletRecipientsListState.Empty -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "No recipients found",
                        style = ElementTheme.typography.fontBodyLgMedium,
                    )
                }
            }
            WalletRecipientsListState.Skeleton -> SkeletonView(10)
            is WalletRecipientsListState.Recipients ->
                RecipientsView(
                    recipients = state.recipientsListState.recipients,
                    onRecipientSelected = { recipient ->
                        state.eventSink(TransferTokenEvents.RecipientSelected(recipient))
                    }
                )
            else -> {}
        }
    }
}

@Composable
fun SearchRecipientTextField(
    onQueryChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val query = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ElementTheme.colors.bgCanvasDefault,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp)
            .focusRequester(focusRequester),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "To:",
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.zeroBrandColor
        )
        TextField(
            modifier = Modifier.weight(1f),
            value = TextFieldValue(query.value, selection = TextRange(query.value.length)),
            onValueChange = { value ->
                query.value = value.text
                onQueryChanged(value.text)
            },
            placeholder = { Text("Name, ZNS or Address", style = ElementTheme.typography.fontBodyLgRegular) },
            singleLine = true,
            maxLines = 1,
            textStyle = ElementTheme.typography.fontBodyLgRegular,
            shape = RectangleShape,
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        if (query.value.isNotBlank()) {
            Icon(
                modifier = Modifier.clickable {
                    query.value = ""
                    onQueryChanged("")
                },
                imageVector = CompoundIcons.Close(),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun SkeletonView(count: Int, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(count) { index ->
            item {
                RecipientPlaceholderRow()
            }
        }
    }
}

@Composable
private fun RecipientPlaceholderRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AvatarSize.RoomListItem.dp)
                .background(color = ElementTheme.colors.placeholderBackground, shape = CircleShape)
        )

        Column(horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                PlaceholderAtom(
                    width = 100.dp,
                    height = 7.dp
                )
                Spacer(Modifier.width(8.dp))
                PlaceholderAtom(
                    width = 100.dp,
                    height = 7.dp
                )
            }
            Spacer(Modifier.height(8.dp))
            PlaceholderAtom(
                modifier = Modifier.padding(horizontal = 12.dp),
                width = 200.dp,
                height = 7.dp
            )
        }
    }
}

@Composable
private fun RecipientsView(
    recipients: List<ZeroWalletRecipient>,
    modifier: Modifier = Modifier,
    onRecipientSelected: (ZeroWalletRecipient) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = recipients, key = { recipient -> recipient.userId }) { recipient ->
            RecipientRow(recipient) {
                onRecipientSelected(recipient)
            }
        }
    }
}

@Composable
private fun RecipientRow(
    recipient: ZeroWalletRecipient,
    onTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarData = AvatarData(
                id = recipient.userId,
                name = recipient.name,
                url = recipient.profileImage,
                size = AvatarSize.UserListItem
            ),
            avatarType = AvatarType.User
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                recipient.name?.let {
                    Text(
                        text = it,
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.size(8.dp))
                recipient.primaryZid?.let {
                    Text(
                        text = it,
                        style = ElementTheme.typography.fontBodyMdRegular,
                        color = ElementTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.size(4.dp))
            ZeroWalletUtil.walletAddressDisplayText(recipient.publicAddress)?.let {
                Text(
                    text = it,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun SelectRecipientViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    SelectRecipientView(state)
}

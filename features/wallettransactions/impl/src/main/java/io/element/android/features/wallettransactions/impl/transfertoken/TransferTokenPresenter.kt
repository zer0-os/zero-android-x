/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.config.ZeroConfig
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransferTokenPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<TransferTokenState> {
    @Composable
    override fun present(): TransferTokenState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val currentUser = client.userProfile.collectAsState()
        val flowStep = rememberSaveable { mutableStateOf(TransferTokenFlowStep.RECIPIENT) }

        var recipientSearchQuery by rememberSaveable { mutableStateOf<String?>(null) }
        val recipientListState: MutableState<WalletRecipientsListState> = remember { mutableStateOf(WalletRecipientsListState.None) }
        val selectedRecipient: MutableState<ZeroWalletRecipient?> = remember { mutableStateOf(null) }

        val meowPrice: MutableState<ZeroMeowPrice?> = remember { mutableStateOf(null) }
        val walletTokensListState: MutableState<WalletTokensListState> = remember {
            mutableStateOf(WalletTokensListState.Skeleton(10))
        }
        val walletTokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?> = remember {
            mutableStateOf(null)
        }
        val selectedToken: MutableState<ZeroWalletToken?> = remember { mutableStateOf(null) }

        val transferAmount = rememberSaveable { mutableStateOf<String?>(null) }
        val transferReceipt = rememberSaveable { mutableStateOf<ZeroWalletTransactionReceipt?>(null) }

        LaunchedEffect(Unit) {
            currentUser.value.walletAddress?.let {
                loadMeowPrice(meowPrice)
                loadTokens(it, emptyList(), walletTokensListState, walletTokenPaginationParams)
            }
        }

        LaunchedEffect(recipientSearchQuery) {
            if (recipientSearchQuery == null) return@LaunchedEffect
            // debounce search query
            delay(300)
            //search for recipient
            searchRecipient(recipientSearchQuery, recipientListState)
        }

        fun handleEvents(event: TransferTokenEvents) {
            when (event) {
                is TransferTokenEvents.ToState -> {
                    flowStep.value = event.state
                }
                is TransferTokenEvents.SearchRecipient -> {
                    recipientSearchQuery = event.query
                }
                is TransferTokenEvents.RecipientSelected -> {
                    flowStep.value = TransferTokenFlowStep.TOKEN
                    selectedRecipient.value = event.recipient
                }
                is TransferTokenEvents.TokenSelected -> {
                    flowStep.value = TransferTokenFlowStep.CONFIRMATION
                    selectedToken.value = event.token
                }
                is TransferTokenEvents.LoadMoreTokens -> {
                    currentUser.value.walletAddress?.let {
                        coroutineScope.loadTokens(
                            walletAddress = it,
                            currentList = event.currentTokens,
                            tokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams
                        )
                    }
                }
                is TransferTokenEvents.ConfirmTransaction -> {
                    flowStep.value = TransferTokenFlowStep.IN_PROGRESS
                    transferAmount.value = event.amount
                    coroutineScope.transferToken(
                        currentUser.value,
                        selectedRecipient.value,
                        selectedToken.value,
                        transferAmount.value,
                        transferReceipt,
                        flowStep
                    )
                }
                is TransferTokenEvents.ViewTransaction -> {
                    context.openExternalUri(event.url)
                }
            }
        }

        return TransferTokenState(
            flowStep = flowStep.value,
            currentUser = currentUser.value,
            recipientsListState = recipientListState.value,
            recipient = selectedRecipient.value,
            token = selectedToken.value,
            tokensListState = walletTokensListState.value,
            tokensPaginationParams = walletTokenPaginationParams.value,
            meowPrice = meowPrice.value,
            transferAmount = transferAmount.value,
            transactionReceipt = transferReceipt.value,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.searchRecipient(query: String?, recipientListState: MutableState<WalletRecipientsListState>) = launch {
        val query = query ?: return@launch
        if (query.isNotBlank()) {
            recipientListState.value = WalletRecipientsListState.Skeleton
            client.searchWalletRecipient(query)
                .onSuccess {
                    if (it.isEmpty()) {
                        recipientListState.value = WalletRecipientsListState.Empty
                    } else {
                        recipientListState.value = WalletRecipientsListState.Recipients(it.toPersistentList())
                    }
                }
                .onFailure {
                    recipientListState.value = WalletRecipientsListState.Empty
                }
        } else {
            recipientListState.value = WalletRecipientsListState.None
        }
    }

    private fun CoroutineScope.loadMeowPrice(meowPrice: MutableState<ZeroMeowPrice?>) = launch {
        client.getMeowPrice()
            .onSuccess {
                meowPrice.value = it
            }
    }

    private fun CoroutineScope.loadTokens(
        walletAddress: String,
        currentList: List<ZeroWalletToken>,
        tokensListState: MutableState<WalletTokensListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>
    ) = launch {
        tokensListState.value = WalletTokensListState.Skeleton(10)
        client.getWalletTokens(
            walletAddress = walletAddress,
            chainId = ZeroConfig.ZERO_WALLET_ZCHAIN_ID,
            paginationParams = tokenPaginationParams.value
        ).onSuccess { result ->
            val newList = mutableListOf<ZeroWalletToken>().apply {
                addAll(currentList)
                addAll(result.tokens)
            }.distinctBy { token -> token.tokenAddress }
            tokensListState.value = WalletTokensListState.Tokens(newList.toPersistentList())
            tokenPaginationParams.value = result.paginationParams
        }.onFailure {
            tokensListState.value = WalletTokensListState.Empty
        }
    }

    private fun CoroutineScope.transferToken(
        sender: MatrixUser,
        recipient: ZeroWalletRecipient?,
        token: ZeroWalletToken?,
        amount: String?,
        transferReceiptFlow: MutableState<ZeroWalletTransactionReceipt?>,
        flowStep: MutableState<TransferTokenFlowStep>
    ) = launch {
        val recipient = recipient ?: return@launch
        val token = token ?: return@launch
        val senderWalletAddress = sender.walletAddress ?: return@launch
        val amount = amount ?: return@launch
        client.transferToken(
            sender = senderWalletAddress,
            recipient = recipient.publicAddress,
            chainId = token.chainId,
            amount = amount,
            token = token.tokenAddress,
        ).onSuccess {
            transferReceiptFlow.value = it
            flowStep.value = TransferTokenFlowStep.COMPLETED
        }.onFailure {
            flowStep.value = TransferTokenFlowStep.ERROR
        }
    }
}

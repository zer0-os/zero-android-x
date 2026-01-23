/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.nft

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFT

@AssistedInject
class NFTDetailsPresenter(
    private val client: MatrixClient,
    @Assisted private val nft: ZeroWalletNFT,
) : Presenter<NFTDetailsState> {

    @AssistedFactory
    interface Factory {
        fun create(nft: ZeroWalletNFT): NFTDetailsPresenter
    }

    @Composable
    override fun present(): NFTDetailsState {
        return NFTDetailsState(nft)
    }
}

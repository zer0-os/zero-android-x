/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import kotlinx.parcelize.Parcelize

interface WalletTransactionsEntryPoint : FeatureEntryPoint {
    sealed interface TransactionType : Parcelable {
        @Parcelize
        data object Token : TransactionType

        @Parcelize
        data object NFT : TransactionType
    }

    data class Params(val transactionType: TransactionType) : NodeInputs
    fun nodeBuilder(parentNode: Node, buildContext: BuildContext): NodeBuilder

    interface NodeBuilder {
        fun params(params: Params): NodeBuilder
        fun callback(callback: Callback): NodeBuilder
        fun build(): Node
    }

    interface Callback : Plugin
}

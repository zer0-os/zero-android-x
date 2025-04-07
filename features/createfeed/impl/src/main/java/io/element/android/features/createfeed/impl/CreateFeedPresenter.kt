/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import javax.inject.Inject

class CreateFeedPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<CreateFeedState> {
    @Composable
    override fun present(): CreateFeedState {
        val coroutineScope = rememberCoroutineScope()

        fun handleEvents(event: CreateFeedEvents) {

        }

        return CreateFeedState(
            feedText = "",
            eventSink = ::handleEvents
        )
    }
}

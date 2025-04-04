/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

class FeedDetailsPresenter @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted private val feed: ZeroFeed
) : Presenter<FeedDetailsState> {

    @AssistedFactory
    interface Factory {
        fun create(feed: ZeroFeed): FeedDetailsPresenter
    }

    @Composable
    override fun present(): FeedDetailsState {
        val coroutineScope = rememberCoroutineScope()

        return FeedDetailsState(feed)
    }
}

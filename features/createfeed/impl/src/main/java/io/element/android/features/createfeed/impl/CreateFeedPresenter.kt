/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateFeedPresenter @Inject constructor(
    private val client: MatrixClient,
) : Presenter<CreateFeedState> {
    @Composable
    override fun present(): CreateFeedState {
        val coroutineScope = rememberCoroutineScope()

        val genericActionState: MutableState<AsyncData<Unit>> = remember { mutableStateOf(AsyncData.Uninitialized) }
        val matrixUser = client.userProfile.collectAsState()
        val newFeedText: MutableState<String> = remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            client.getUserProfile()
        }

        fun handleEvents(event: CreateFeedEvents) {
            when (event) {
                is CreateFeedEvents.PostTextChanged -> newFeedText.value = event.text
                CreateFeedEvents.CreatePost -> coroutineScope.createNewFeed(newFeedText, genericActionState)
                CreateFeedEvents.HideError -> genericActionState.value = AsyncData.Uninitialized
            }
        }

        return CreateFeedState(
            feedText = newFeedText.value,
            matrixUser = matrixUser.value,
            eventSink = ::handleEvents,
            genericActionState = genericActionState.value
        )
    }

    private fun CoroutineScope.createNewFeed(
        newFeedText: MutableState<String>,
        genericActionState: MutableState<AsyncData<Unit>>
    ) = launch {
        genericActionState.value = AsyncData.Loading()
        val postText = newFeedText.value
        client.createNewFeed(postText, null)
            .onSuccess {
                genericActionState.value = AsyncData.Success(Unit)
            }
            .onFailure { error ->
                genericActionState.value = AsyncData.Failure(error)
            }
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.CreateFeedMediaAttachment
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.support.zero.common.extension.localFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateFeedPresenter @Inject constructor(
    private val client: MatrixClient,
    private val mediaPickerProvider: PickerProvider,
) : Presenter<CreateFeedState> {
    @Composable
    override fun present(): CreateFeedState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val matrixUser = client.userProfile.collectAsState()
        val newFeedText: MutableState<String> = remember { mutableStateOf("") }
        val newFeedAttachmentState: MutableState<CreateFeedMediaAttachment?> = remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            client.getUserProfile()
        }

        val galleryMediaPicker = mediaPickerProvider.registerGalleryPicker { uri, mimeType ->
            handlePickedMedia(context, newFeedAttachmentState, uri, mimeType)
        }

        fun handleEvents(event: CreateFeedEvents) {
            when (event) {
                is CreateFeedEvents.PostTextChanged -> newFeedText.value = event.text
                CreateFeedEvents.CreatePost -> coroutineScope.createNewFeed(
                    newFeedText, newFeedAttachmentState.value, genericActionState
                )
                CreateFeedEvents.HideError -> genericActionState.value = AsyncAction.Uninitialized
                CreateFeedEvents.SelectMedia -> {
                    coroutineScope.launch {
                        galleryMediaPicker.launch()
                    }
                }
                CreateFeedEvents.RemoveMedia -> newFeedAttachmentState.value = null
            }
        }

        return CreateFeedState(
            feedText = newFeedText.value,
            matrixUser = matrixUser.value,
            mediaAttachment = newFeedAttachmentState.value,
            eventSink = ::handleEvents,
            genericActionState = genericActionState.value
        )
    }

    private fun CoroutineScope.createNewFeed(
        newFeedText: MutableState<String>,
        feedAttachment: CreateFeedMediaAttachment?,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        val postText = newFeedText.value
        client.createNewFeed(postText, feedAttachment, null)
            .onSuccess {
                genericActionState.value = AsyncAction.Success(Unit)
            }
            .onFailure { error ->
                genericActionState.value = AsyncAction.Failure(error)
            }
    }


    private fun handlePickedMedia(
        context: Context,
        newFeedAttachmentState: MutableState<CreateFeedMediaAttachment?>,
        uri: Uri?,
        mimeType: String? = null,
    ) {
        if (uri != null && mimeType != null) {
            val mediaFile = uri.localFile(context)
            mediaFile?.let {
                val media = CreateFeedMediaAttachment(it, mimeType)
                newFeedAttachmentState.value = media
            }
        }
    }
}

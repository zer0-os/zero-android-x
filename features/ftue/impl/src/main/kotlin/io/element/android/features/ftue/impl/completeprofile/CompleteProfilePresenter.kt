/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.androidutils.file.TemporaryUriDeleter
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaPreProcessor
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import io.element.android.support.zero.common.util.ValidationUtil
import io.element.android.support.zero.common.util.ZeroCreateAccountInviteHolder
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AssistedInject
class CompleteProfilePresenter constructor(
    @Assisted private val callback: CompleteProfileNode.Callback,
    private val client: MatrixClient,
    private val mediaPickerProvider: PickerProvider,
    private val mediaPreProcessor: MediaPreProcessor,
    private val temporaryUriDeleter: TemporaryUriDeleter,
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
) : Presenter<CompleteProfileState> {
    private val cameraPermissionPresenter: PermissionsPresenter = permissionsPresenterFactory.create(android.Manifest.permission.CAMERA)
    private var pendingPermissionRequest = false

    @AssistedFactory
    interface Factory {
        fun create(callback: CompleteProfileNode.Callback): CompleteProfilePresenter
    }

    @Composable
    override fun present(): CompleteProfileState {
        val cameraPermissionState = cameraPermissionPresenter.present()
        var userAvatarUri: Uri? by rememberSaveable { mutableStateOf(null) }
        var userDisplayName by rememberSaveable { mutableStateOf("") }

        val cameraPhotoPicker = mediaPickerProvider.registerCameraPhotoPicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(userAvatarUri)
                    userAvatarUri = uri
                }
            }
        )
        val galleryImagePicker = mediaPickerProvider.registerGalleryImagePicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(userAvatarUri)
                    userAvatarUri = uri
                }
            }
        )

        val avatarActions by remember(userAvatarUri) {
            derivedStateOf {
                listOfNotNull(
                    AvatarAction.TakePhoto,
                    AvatarAction.ChoosePhoto,
                ).toImmutableList()
            }
        }

        LaunchedEffect(cameraPermissionState.permissionGranted) {
            if (cameraPermissionState.permissionGranted && pendingPermissionRequest) {
                pendingPermissionRequest = false
                cameraPhotoPicker.launch()
            }
        }

        val saveAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val localCoroutineScope = rememberCoroutineScope()

        fun handleEvents(event: CompleteProfileEvents) {
            when (event) {
                CompleteProfileEvents.Submit -> {
                    localCoroutineScope.saveProfile(userDisplayName, userAvatarUri, saveAction)
                }
                CompleteProfileEvents.Clear -> {
                    saveAction.value = AsyncAction.Uninitialized
                }
                CompleteProfileEvents.ProfileUpdated -> {
                    callback.onProfileUpdated()
                }
                is CompleteProfileEvents.SetDisplayName -> userDisplayName = event.name
                is CompleteProfileEvents.HandleAvatarAction -> {
                    when (event.action) {
                        AvatarAction.ChoosePhoto -> galleryImagePicker.launch()
                        AvatarAction.TakePhoto -> if (cameraPermissionState.permissionGranted) {
                            cameraPhotoPicker.launch()
                        } else {
                            pendingPermissionRequest = true
                            cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                        }
                        AvatarAction.Remove -> {
                            temporaryUriDeleter.delete(userAvatarUri)
                            userAvatarUri = null
                        }
                    }
                }
            }
        }

        val canSave = remember(userDisplayName) {
            userDisplayName.isNotBlank() &&
                (ValidationUtil.liesInLengthRange(min = 3, max = 24, input = userDisplayName) == null)
        }

        return CompleteProfileState(
            displayName = "",
            userAvatarUrl = userAvatarUri,
            avatarActions = avatarActions,
            saveButtonEnabled = canSave && saveAction.value !is AsyncAction.Loading,
            saveAction = saveAction.value,
            cameraPermissionState = cameraPermissionState,
            eventSink = ::handleEvents
        )
    }

    private fun CoroutineScope.saveProfile(
        name: String,
        avatarUri: Uri?,
        saveAction: MutableState<AsyncAction<Unit>>,
    ) = launch {
        saveAction.value = AsyncAction.Loading
        var avatarData: ByteArray? = null
        var avatarMimeType: String? = null
        if (avatarUri != null) {
            val preprocessed = mediaPreProcessor.process(
                uri = avatarUri,
                mimeType = MimeTypes.Jpeg,
                deleteOriginal = false,
                mediaOptimizationConfig = mediaOptimizationConfigProvider.get()
            ).getOrThrow()
            avatarMimeType = MimeTypes.Jpeg
            avatarData = preprocessed.file.readBytes()
        }
        client.completeZeroUserProfile(
            inviteCode = ZeroCreateAccountInviteHolder.inviteCode,
            displayName = name,
            mimeType = avatarMimeType,
            avatarData = avatarData
        ).onSuccess {
            saveAction.value = AsyncAction.Success(Unit)
        }.onFailure {
            saveAction.value = AsyncAction.Failure(it)
        }
    }
}

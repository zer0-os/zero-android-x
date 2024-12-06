/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.permissions.api.aPermissionsState
import kotlinx.collections.immutable.persistentListOf

open class CompleteProfileStateProvider : PreviewParameterProvider<CompleteProfileState> {
    override val values: Sequence<CompleteProfileState>
        get() = sequenceOf(
            aCompleteProfileState(),
        )
}

fun aCompleteProfileState() = CompleteProfileState(
    displayName = "",
    userAvatarUrl = null,
    avatarActions = persistentListOf(),
    saveAction = AsyncAction.Uninitialized,
    saveButtonEnabled = true,
    cameraPermissionState = aPermissionsState(showDialog = false),
    eventSink = {}
)

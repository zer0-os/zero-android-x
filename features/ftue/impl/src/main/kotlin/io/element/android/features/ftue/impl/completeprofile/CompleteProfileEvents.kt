/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import io.element.android.libraries.matrix.ui.media.AvatarAction

sealed interface CompleteProfileEvents {
    data object Submit : CompleteProfileEvents
    data class SetDisplayName(val name: String) : CompleteProfileEvents
    data class HandleAvatarAction(val action: AvatarAction) : CompleteProfileEvents
}

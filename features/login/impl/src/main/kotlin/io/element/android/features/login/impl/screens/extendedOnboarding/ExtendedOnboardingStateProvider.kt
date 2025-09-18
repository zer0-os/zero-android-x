/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

open class ExtendedOnboardingStateProvider : PreviewParameterProvider<ExtendedOnboardingState> {
    override val values: Sequence<ExtendedOnboardingState>
        get() = sequenceOf(
            aExtendedOnboardingState()
        )
}

fun aExtendedOnboardingState(
    forgotPasswordEmail: String = "example@user.com",
    actionState: AsyncAction<Unit> = AsyncAction.Uninitialized
) = ExtendedOnboardingState(
    forgotPasswordEmail = forgotPasswordEmail,
    actionState = actionState,
    eventSink = {}
)

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.features.login.impl.error.loginError
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun ExtendedViewErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    ErrorDialog(
        title = stringResource(id = CommonStrings.dialog_title_error),
        content = stringResource(loginError(error)),
        onSubmit = onDismiss
    )
}

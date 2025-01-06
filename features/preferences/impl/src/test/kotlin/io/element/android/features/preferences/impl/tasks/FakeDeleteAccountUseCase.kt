/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.preferences.impl.tasks

class FakeDeleteAccountUseCase: DeleteAccountUseCase {

    override suspend fun invoke(): Boolean {
        return true
    }
}

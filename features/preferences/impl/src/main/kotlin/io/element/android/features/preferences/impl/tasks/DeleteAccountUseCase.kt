/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.preferences.impl.tasks

import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface DeleteAccountUseCase {
    suspend operator fun invoke(): Boolean
}

@ContributesBinding(SessionScope::class)
class DefaultDeleteAccountUseCase @Inject constructor(
    private val matrixClient: MatrixClient,
    private val coroutineDispatchers: CoroutineDispatchers,
) : DeleteAccountUseCase {
    override suspend fun invoke() = withContext(coroutineDispatchers.io) {
        matrixClient.deleteUserAccount().isSuccess
    }
}

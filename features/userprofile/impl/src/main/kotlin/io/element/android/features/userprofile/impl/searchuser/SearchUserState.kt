/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.searchuser

import androidx.compose.runtime.Immutable
import io.element.android.features.startchat.impl.userlist.UserListState

@Immutable
data class SearchUserState(
    val userListState: UserListState,
)

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.searchuser

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Inject
import io.element.android.features.startchat.impl.userlist.SelectionMode
import io.element.android.features.startchat.impl.userlist.UserListDataStore
import io.element.android.features.startchat.impl.userlist.UserListPresenter
import io.element.android.features.startchat.impl.userlist.UserListPresenterArgs
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.usersearch.api.UserRepository

@Inject
class SearchUserPresenter constructor(
    presenterFactory: UserListPresenter.Factory,
    userRepository: UserRepository,
    userListDataStore: UserListDataStore,
) : Presenter<SearchUserState> {

    private val presenter = presenterFactory.create(
        UserListPresenterArgs(
            selectionMode = SelectionMode.Single,
        ),
        userRepository,
        userListDataStore,
    )

    @Composable
    override fun present(): SearchUserState {
        val userListState = presenter.present()

        return SearchUserState(
            userListState = userListState
        )
    }
}

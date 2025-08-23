/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.searchuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.startchat.impl.components.UserListView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.core.UserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserView(
    modifier: Modifier = Modifier,
    state: SearchUserState,
    onBackClick: () -> Unit = {},
    onUserSelected: (UserId) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            if (!state.userListState.isSearchActive) {
                SearchUserViewTopbar(onCloseClick = onBackClick)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserListView(
                modifier = Modifier.fillMaxWidth(),
                state = state.userListState,
                onSelectUser = { onUserSelected(it.userId) },
                onDeselectUser = { },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchUserViewTopbar(
    onCloseClick: () -> Unit,
) {
    TopAppBar(
        titleStr = "",
        navigationIcon = {
            BackButton(
                imageVector = CompoundIcons.Close(),
                onClick = onCloseClick,
            )
        }
    )
}

@PreviewsDayNight
@Composable
internal fun SearchUserViewPreview(@PreviewParameter(SearchUserStateProvider::class) state: SearchUserState) =
    ElementPreview {
        SearchUserView(
            state = state,
            onBackClick = {}
        )
    }

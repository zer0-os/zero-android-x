/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.support.zero.common.extension.getActivity
import io.element.android.support.zero.common.ui.ZeroAuthScreensBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileView(
    state: CompleteProfileState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val exitApp: () -> Unit = {
        context.getActivity()?.finishAffinity()
    }

    BackHandler { exitApp() }

    fun submit() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)

        //state.eventSink(CompleteProfileEvents.Submit)
    }

    ZeroAuthScreensBackground(isLoading = false) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(io.element.android.support.zero.R.string.create_account)) },
                    navigationIcon = { },
                    colors = TopAppBarDefaults
                        .centerAlignedTopAppBarColors()
                        .copy(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(state = scrollState)
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            ) {

            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun CompleteProfileViewPreview(
    @PreviewParameter(CompleteProfileStateProvider::class) state: CompleteProfileState
) {
    ElementPreview {
        CompleteProfileView(
            state = state
        )
    }
}

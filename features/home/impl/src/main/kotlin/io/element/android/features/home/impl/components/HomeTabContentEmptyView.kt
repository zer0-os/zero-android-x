/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeTabContentEmptyView(
    modifier: Modifier = Modifier,
    text: String = "No content available",
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = text,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

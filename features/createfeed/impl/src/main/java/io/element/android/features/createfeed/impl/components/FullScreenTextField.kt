/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FullScreenTextField(
    text: String,
    placeholderText: String = "",
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text(placeholderText) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            singleLine = false,
            maxLines = Int.MAX_VALUE
        )
    }
}

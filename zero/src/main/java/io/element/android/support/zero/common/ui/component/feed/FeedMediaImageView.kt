/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui.component.feed

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import java.io.File

@Composable
fun FeedMediaImageView(
    url: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        modifier = modifier.clickable{ onTap() },
        model = url,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        contentDescription = null,
    )
}

@Composable
fun FeedMediaImageView(
    file: File,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        modifier = modifier.clickable{ onTap() },
        model = file,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        contentDescription = null,
    )
}

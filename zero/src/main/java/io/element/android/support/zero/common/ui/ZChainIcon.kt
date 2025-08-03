/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.support.zero.R

@Composable
fun ZChainIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.size(12.dp),
        painter = painterResource(R.drawable.ic_zchain),
        contentDescription = null
    )
}

@PreviewsDayNight
@Composable
fun ZChainIconPreview() = ElementPreview {
    ZChainIcon()
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.support.zero.R

@Composable
fun WalletChainIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    size: Dp = 16.dp,
) {
    Image(
        modifier = modifier
            .size(size),
        imageVector = icon,
        contentDescription = null,
    )
}

@PreviewsDayNight
@Composable
fun WalletChainIconPreview() = ElementPreview {
    WalletChainIcon(icon = ImageVector.vectorResource(R.drawable.ic_chain_z))
}


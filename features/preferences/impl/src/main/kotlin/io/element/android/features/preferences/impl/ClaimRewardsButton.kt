/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.TextButton

@Composable
fun ClaimRewardsButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    TextButton(
        modifier = modifier,
        text = "Claim Rewards",
        onClick = onClick,
        enabled = enabled,
        size = ButtonSize.Small,
        leadingIcon = IconSource.Vector(
            vector = ImageVector.vectorResource(io.element.android.support.zero.R.drawable.ic_claim_rewards)
        )
    )
}

@PreviewsDayNight
@Composable
fun ClaimRewardsButtonPreview() = ElementPreview {
    ClaimRewardsButton()
}

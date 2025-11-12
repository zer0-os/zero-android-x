/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.FloatingActionButton
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor

@Composable
fun HomeFabButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        modifier = modifier,
        shape = shape,
        containerColor = ElementTheme.colors.zeroBrandColor,
        onClick = onClick
    ) {
        Icon(
            imageVector = CompoundIcons.Plus(),
            contentDescription = stringResource(id = R.string.screen_roomlist_a11y_create_message),
            tint = ElementTheme.colors.iconOnSolidPrimary,
        )
    }
}

@PreviewsDayNight
@Composable
fun HomeFabButtonPreview() = ElementTheme {
    HomeFabButton()
}

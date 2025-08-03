/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.support.zero.R
import kotlinx.coroutines.delay

private const val MAX_MEOW_LIMIT = 100

@Composable
fun FeedMeowActionButton(
    meowCount: String,
    highlighted: Boolean = false,
    enabled: Boolean = true,
    onAddMeowToFeed: (Int) -> Unit = {},
) {
    var count by remember { mutableIntStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    val onPressReleased: () -> Unit = {
        isPressed = false
        onAddMeowToFeed(count)
        count = 0
    }

    LaunchedEffect(isPressed) {
        while (isPressed) {
            if (count < MAX_MEOW_LIMIT) {
                count++
                delay(250)
            } else {
                onPressReleased()
            }
        }
    }

    val tint = if (highlighted) ElementTheme.colors.zeroBrandColor
    else ElementTheme.colors.textSecondary
    Row(
        modifier = if (enabled) {
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease() // Waits for the user to lift the finger
                            onPressReleased()
                        }
                    )
                }
        } else Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.ic_post_meow),
            contentDescription = null,
            tint = tint
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = meowCount,
            style = ElementTheme.typography.fontBodyMdRegular,
            color = tint
        )
        if (isPressed && count > 0) {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "+$count",
                style = ElementTheme.typography.fontBodyMdMedium,
                color = ElementTheme.colors.zeroBrandColor
            )
        }
    }
}

/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SwipeToConfirmButton(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit
) {
    val buttonHeight = 56.dp
    val knobSize = 56.dp
    val horizontalPadding = 16.dp

    val dragOffset = remember { mutableFloatStateOf(0f) }
    var isConfirmed by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .height(buttonHeight)
    ) {
        val fullWidth = constraints.maxWidth.toFloat()
        val knobPx = with(LocalDensity.current) { knobSize.toPx() }
        val maxOffset = fullWidth - knobPx
        val threshold = maxOffset

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(buttonHeight / 4))
                .background(ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f))
                .border(1.dp, ElementTheme.colors.zeroBrandColor, RoundedCornerShape(buttonHeight / 4)) // border color = .zero.bgAccentRest
        )

        Text(
            text = if (isConfirmed) "Confirmed" else "Swipe to Confirm",
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if (isConfirmed) 1f else 1f - (dragOffset.floatValue / threshold).coerceIn(0f, 1f)),
            color = ElementTheme.colors.zeroBrandColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragOffset.floatValue.roundToInt(), 0) }
                    .size(knobSize)
                    .clip(RoundedCornerShape(knobSize / 4))
                    .background(ElementTheme.colors.zeroBrandColor) // .zero.bgAccentRest
                    .pointerInput(isConfirmed) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (!isConfirmed) {
                                    dragOffset.floatValue =
                                        (dragOffset.floatValue + dragAmount.x).coerceIn(0f, maxOffset)
                                }
                            },
                            onDragEnd = {
                                if (!isConfirmed) {
                                    if (dragOffset.floatValue >= threshold) {
                                        isConfirmed = true
                                        dragOffset.floatValue = maxOffset
                                        onConfirm()
                                        // optional: haptic feedback if needed
                                    } else {
                                        dragOffset.floatValue = 0f
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isConfirmed) Icons.Default.Check else Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

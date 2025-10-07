/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BorderLoadingView(
    modifier: Modifier = Modifier,
    baseColor: Color = Color.Cyan,
    highlightColor: Color = Color.White,
    strokeWidth: Dp = 2.dp,
    glowIntensity: Float = 0.7f,
    speedMillis: Int = 4000,
    isVisible: Boolean = true
) {
    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition(label = "borderGlowAnim")

    // Primary moving dash
    val progress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(speedMillis, easing = LinearEasing)
        ),
        label = "progress1"
    )

    // Secondary moving dash (half cycle offset)
    val progress2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(speedMillis, easing = LinearEasing)
        ),
        label = "progress2"
    )

    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val perimeter = 2 * (w + h)
        val dashLength = perimeter * 0.15f

        /** 🌌 1️⃣ Static glowing border */
        val staticBrush = Brush.linearGradient(
            colors = listOf(
                baseColor.copy(alpha = glowIntensity * 0.2f),
                baseColor.copy(alpha = glowIntensity),
                baseColor.copy(alpha = glowIntensity * 0.2f)
            ),
            start = Offset.Zero,
            end = Offset(w, h)
        )

        drawRoundRect(
            brush = staticBrush,
            topLeft = Offset.Zero,
            size = size,
            style = Stroke(width = strokePx)
        )

        /** ⚡ 2️⃣ Moving highlight lines */
        val highlightBrush = Brush.linearGradient(
            colors = listOf(
                highlightColor.copy(alpha = 0f),
                highlightColor.copy(alpha = 1f),
                highlightColor.copy(alpha = 0f)
            ),
        )

        fun drawSegment(start: Float, end: Float) {
            var remainingStart = start
            var remainingEnd = end

            // top edge
            var edgeLength = w
            if (remainingStart < edgeLength && remainingEnd > 0f) {
                val s = remainingStart.coerceIn(0f, edgeLength)
                val e = remainingEnd.coerceIn(0f, edgeLength)
                drawLine(highlightBrush, Offset(s, 0f), Offset(e, 0f), strokePx, cap = StrokeCap.Round)
            }
            remainingStart -= edgeLength; remainingEnd -= edgeLength

            // right edge
            edgeLength = h
            if (remainingStart < edgeLength && remainingEnd > 0f) {
                val s = remainingStart.coerceIn(0f, edgeLength)
                val e = remainingEnd.coerceIn(0f, edgeLength)
                drawLine(highlightBrush, Offset(w, s), Offset(w, e), strokePx, cap = StrokeCap.Round)
            }
            remainingStart -= edgeLength; remainingEnd -= edgeLength

            // bottom edge
            edgeLength = w
            if (remainingStart < edgeLength && remainingEnd > 0f) {
                val s = remainingStart.coerceIn(0f, edgeLength)
                val e = remainingEnd.coerceIn(0f, edgeLength)
                drawLine(highlightBrush, Offset(w - s, h), Offset(w - e, h), strokePx, cap = StrokeCap.Round)
            }
            remainingStart -= edgeLength; remainingEnd -= edgeLength

            // left edge
            edgeLength = h
            if (remainingStart < edgeLength && remainingEnd > 0f) {
                val s = remainingStart.coerceIn(0f, edgeLength)
                val e = remainingEnd.coerceIn(0f, edgeLength)
                drawLine(highlightBrush, Offset(0f, h - s), Offset(0f, h - e), strokePx, cap = StrokeCap.Round)
            }
        }

        // 🔹 Draw first dash
        val distance1 = progress1 * perimeter
        val start1 = distance1 % perimeter
        val end1 = (distance1 + dashLength) % perimeter
        if (end1 > start1) {
            drawSegment(start1, end1)
        } else {
            drawSegment(start1, perimeter)
            drawSegment(0f, end1)
        }

        // 🔹 Draw second dash
        val distance2 = progress2 * perimeter
        val start2 = distance2 % perimeter
        val end2 = (distance2 + dashLength) % perimeter
        if (end2 > start2) {
            drawSegment(start2, end2)
        } else {
            drawSegment(start2, perimeter)
            drawSegment(0f, end2)
        }
    }
}

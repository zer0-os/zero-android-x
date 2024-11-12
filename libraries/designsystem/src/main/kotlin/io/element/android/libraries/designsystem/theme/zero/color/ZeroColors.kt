package io.element.android.libraries.designsystem.theme.zero.color

import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors

internal val zeroAccentColor = Color(0xFF01F4CB)

val SemanticColors.zeroBrandColor
    get() = zeroAccentColor

val SemanticColors.zeroBrandColorAlpha10
    get() = zeroAccentColor.copy(alpha = 0.10f)

val SemanticColors.zeroBrandColorAlpha15
    get() = zeroAccentColor.copy(alpha = 0.15f)

val SemanticColors.zeroBrandColorAlpha20
    get() = zeroAccentColor.copy(alpha = 0.20f)

val SemanticColors.zeroBrandColorAlpha50
    get() = zeroAccentColor.copy(alpha = 0.50f)
